package com.maurogonzalez.services

import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.maurogonzalez.RequestLogging
import com.maurogonzalez.models.{Event, Organizer}
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import io.circe.generic.auto._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Path("/events")
@Api(value = "/events", produces = "application/json" )
case class EventService(var es: mutable.HashMap[String, Event], os: mutable.HashMap[String, Organizer])(implicit system: ActorSystem) extends BaseService with RequestLogging {
  implicit def executor: ExecutionContext = system.dispatcher
  def log = Logging(system, classOf[EventService])

  def routes(): Route = path("events") {
    logRequestResult(loggingMagnet) {
      getEvents ~ createEvent
    }
  }

  @ApiOperation(value = "Return events", notes = "Events", httpMethod = "GET",
    nickname = "events", produces = "application/json", response = classOf[List[Event]])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "organizer", paramType = "query", dataTypeClass = classOf[String])
  ))
  def getEvents: Route = get {
    parameters('organizer.?, 'client.?) {
      (organizer, client) =>
        complete(findEvents(organizer))
    }
  }

  @ApiOperation(value = "Create Event", notes = "Events", httpMethod = "POST",
    nickname = "events", produces = "application/json", consumes = "application/json", response = classOf[Event])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "event", paramType = "body", dataTypeClass = classOf[Event])
  ))
  def createEvent: Route = post {
    entity(as[Event]) { (event: Event) =>
      onComplete({
        println(event.organizerEmail)
        createEvent(event)
      }) {
        case Success(newEvent) =>
          newEvent match {
            case Left(e) => complete(StatusCodes.NotFound, e.getMessage)
            case _ => complete(newEvent)
          }
        case Failure(t) => t match {
          case _ => complete(StatusCodes.InternalServerError, t)
        }
      }
    }

  }

  def createEvent(e: Event): Future[Either[Exception, Event]] = Future {
    os.get(e.organizerEmail) flatMap (o => {
        es += (e.name -> e)
        Some(e)
      }) match {
      case None => Left(new Exception("Organizer not found"))
      case _ => Right(e)
    }
  }

  def findEvents(oE: Option[String]): Future[List[Event]] = Future {
    es.values.toList filter( e => oE match {
      case None => true
      case Some(o) => e.organizerEmail == o
    })
  }

}
