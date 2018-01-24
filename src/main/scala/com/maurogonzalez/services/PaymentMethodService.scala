package com.maurogonzalez.services

import javax.ws.rs.Path

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.server.Directives.{complete, get, logRequestResult, path}
import akka.http.scaladsl.server.Route
import com.maurogonzalez.RequestLogging
import com.maurogonzalez.models.PaymentMethod
import io.swagger.annotations.{Api, ApiOperation}
import io.circe.generic.auto._

import scala.collection.immutable.HashMap
import scala.concurrent.ExecutionContext


@Path("/paymentmethods")
@Api(value = "/paymentmethods", produces = "application/json" )
case class PaymentMethodService(ps: HashMap[String, PaymentMethod])(implicit system: ActorSystem) extends BaseService with RequestLogging {
  implicit def executor: ExecutionContext = system.dispatcher
  def log = Logging(system, classOf[PaymentMethodService])

  @ApiOperation(value = "Return Payment Methods", notes = "paymentmethods", httpMethod = "GET",
    nickname = "paymentmethods", produces = "application/json", response = classOf[List[PaymentMethod]])
  def routes(): Route = path("paymentmethods") {
    logRequestResult("paymentmethods") {
      get {
        complete(ps.values)
      }
    }
  }

}
