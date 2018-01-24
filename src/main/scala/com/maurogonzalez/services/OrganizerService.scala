package com.maurogonzalez.services

import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.maurogonzalez.RequestLogging
import com.maurogonzalez.models.Organizer
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import io.circe.generic.auto._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Path("/organizers")
@Api(value = "/organizers", produces = "application/json" )
case class OrganizerService(var os: mutable.HashMap[String, Organizer])(implicit system: ActorSystem) extends BaseService with RequestLogging {
  implicit def executor: ExecutionContext = system.dispatcher
  def log = Logging(system, classOf[OrganizerService])

  def routes(): Route = path("organizers") {
    logRequestResult(loggingMagnet) {
      getOs ~ createO
    }
  }

  @ApiOperation(value = "Return organizers", notes = "organizers", httpMethod = "GET",
    nickname = "organizers", produces = "application/json", response = classOf[List[Organizer]])
  def getOs: Route = get {
    complete(os.values)
  }

  @ApiOperation(value = "Create organizer", notes = "organizers", httpMethod = "POST",
    nickname = "organizers", produces = "application/json", consumes = "application/json", response = classOf[Organizer])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "organizer", paramType = "body", dataTypeClass = classOf[Organizer])
  ))
  def createO: Route = post {
    entity(as[Organizer]) { (user: Organizer) =>
      onComplete({
        oProcessor(user)
      }) {
        case Success(newUser) =>
          complete(newUser)
        case Failure(t) => t match {
          case _ => complete(StatusCodes.InternalServerError, t)
        }
      }
    }
  }

  def oProcessor(o: Organizer): Future[Organizer] = Future {
    os += (o.email -> o)
    o
  }

}

