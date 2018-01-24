package com.maurogonzalez.services

import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.maurogonzalez.RequestLogging
import com.maurogonzalez.models.Client
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import io.circe.generic.auto._

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Path("/clients")
@Api(value = "/clients", produces = "application/json" )
case class ClientService(var cs: mutable.HashMap[String, Client])(implicit system: ActorSystem) extends BaseService with RequestLogging {
  implicit def executor: ExecutionContext = system.dispatcher
  def log = Logging(system, classOf[ClientService])

  def routes(): Route = path("clients") {
    logRequestResult(loggingMagnet) {
      getClients ~ createClient
    }
  }

  def userProcessor(c: Client): Future[Client] = Future {
    cs += (c.email -> c)
    c
  }

  @ApiOperation(value = "Return clients", notes = "Clients", httpMethod = "GET",
    nickname = "clients", produces = "application/json", response = classOf[List[Client]])
  def getClients: Route = get {
    complete(cs.values)
  }

  @ApiOperation(value = "Create client", notes = "Clients", httpMethod = "POST",
    nickname = "name", produces = "application/json", consumes = "application/json", response = classOf[Client])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "client", paramType = "body", dataTypeClass = classOf[Client])
  ))
  def createClient: Route = post {
    entity(as[Client]) { (user: Client) =>
      onComplete({
        userProcessor(user)
      }) {
        case Success(newUser) =>
          complete(newUser)
        case Failure(t) => t match {
          case _ => complete(StatusCodes.InternalServerError, t)
        }
      }
    }
  }

}
