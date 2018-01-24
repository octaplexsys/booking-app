package com.maurogonzalez.services

import java.util.UUID
import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.maurogonzalez.RequestLogging
import com.maurogonzalez.models._
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import io.circe.generic.auto._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Path("/bookings")
@Api(value = "/bookings", produces = "application/json")
case class BookingService(
                           es: mutable.HashMap[String, Event],
                           os: mutable.HashMap[String, Organizer],
                           cs: mutable.HashMap[String, Client],
                           bs: mutable.HashMap[String, Booking]
                         )(implicit system: ActorSystem) extends BaseService with RequestLogging {
  implicit def executor: ExecutionContext = system.dispatcher
  def log = Logging(system, classOf[BookingService])


  def routes(): Route = path("bookings") {
      logRequestResult(loggingMagnet) {
        createBooking ~ getBookings
      }
    }

  @ApiOperation(value = "Get bookings", notes = "Bookings", httpMethod = "GET",
    nickname = "booking", response = classOf[List[Booking]], produces = "application/json")
  def getBookings: Route = get {
    complete(bs.values)
  }

  @ApiOperation(value = "Create a booking", notes = "Create booking", httpMethod = "POST",
    nickname = "booking", response = classOf[Booking], consumes = "application/json", produces = "application/json")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "Booking", paramType = "body", dataTypeClass = classOf[Booking])
  ))
  def createBooking: Route = post {
    entity(as[Booking]) { b: Booking =>
      onComplete ({
        processBooking(b)
      }) {
        case Success(booking) => booking match {
          case Left(nf) => complete(StatusCodes.NotFound, nf)
          case _ => complete(booking)
        }
        case Failure(t) => t match {
          case _ => complete(StatusCodes.InternalServerError, t)
        }
      }
    }
  }

  def processBooking(b: Booking): Future[Either[String, Booking]] = Future {
    if (cs.get(b.client).isEmpty) Left("Client not found.") else
    es.get(b.eventName) match {
      case None => Left("Event not found.")
      case Some(e) => {
        val price: Option[BigDecimal] = b.price orElse e.price orElse Some(0)
        val st: BigDecimal = b.qty * price.get

        e.commissions orElse os(e.organizerEmail).commissions orElse Some(List(Commission(b.paymentMethodId))) flatMap(
          c => Some {
            c filter(_.paymentMethodId == b.paymentMethodId) match {
              case head :: _ => Right(head)
              case _ => Left(s"Payment Method not found for Event: ${b.eventName}.")
            }
          }
        ) get match {
          case Left(ex) => Left(ex)
          case Right(c) => Right {
            val cm = if (c.percentage.isDefined) st * c.percentage.get else
              c.value getOrElse 0.asInstanceOf[BigDecimal]

            val bk = Booking(
              id = Some(UUID.randomUUID().toString),
              client = b.client,
              eventName = e.name,
              price = price,
              qty = b.qty,
              subTotal = st,
              commission = cm,
              total = st + cm,
              paymentMethodId = b.paymentMethodId
            )
            bs += (bk.id.get -> bk)
            bk
          }

        }
      }
    }
  }
}
