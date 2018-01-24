package com.maurogonzalez.services

import java.lang.management.ManagementFactory

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.maurogonzalez.models.Status
import io.circe.generic.auto._
import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.event.Logging
import com.maurogonzalez.RequestLogging
import io.swagger.annotations._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

@Path("/status")
@Api(value = "/status", produces = "application/json")
case class StatusService()(implicit system: ActorSystem) extends BaseService with RequestLogging {
  implicit def executor: ExecutionContext = system.dispatcher
  def log = Logging(system, service)

  @ApiOperation(value = "Return OK status", notes = "Uptime in millisencods", httpMethod = "GET",
    nickname = "status", response = classOf[Status], produces = "application/json")
  def routes(): Route =
    path("status") {
      logRequestResult(loggingMagnet) {
        get {
          complete(Status(Duration(ManagementFactory.getRuntimeMXBean.getUptime, MILLISECONDS).toString()))
        }
      }
    }
}
