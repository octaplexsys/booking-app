package com.maurogonzalez

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.separateOnSlashes
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.CorsDirectives._
import com.maurogonzalez.bootstrap.InitialValue
import com.maurogonzalez.models.{Booking, Client, Event, Organizer}
import com.maurogonzalez.services._
import com.typesafe.config.{Config, ConfigFactory}

import scala.collection.immutable.HashMap
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object Main extends App with HttpService with RequestLogging {

  implicit def executor = aS.dispatcher
  protected def log = Logging(aS, service)
  implicit val aS: ActorSystem = ActorSystem()
  implicit val aM: ActorMaterializer = ActorMaterializer()
  override def akkaConfig: Config = ConfigFactory.load()

  protected def configureRoutes(system: ActorSystem): Route = {
    val init = new InitialValue

    var cs = new mutable.HashMap[String, Client]
    var os = new mutable.HashMap[String, Organizer]
    var es = new mutable.HashMap[String, Event]
    var bs = new mutable.HashMap[String, Booking]
    val pms = HashMap(init.pms map(p => p.id -> p): _*)

    init.clients foreach ( c => cs += (c.email -> c))

    init.organizers foreach ( o => os += (o.email -> o))

    cors()(StatusService().routes ~ ClientService(cs).routes() ~ OrganizerService(os).routes() ~
      EventService(es, os).routes() ~ PaymentMethodService(pms).routes() ~ BookingService(es, os, cs, bs).routes() ~
      SwaggerDocService(aM, akkaConfig.getString("akka.http.path-prefix"), akkaConfig.getString("swagger.host"), akkaConfig.getString("akka.http.port")).routes)
  }

  def bind(route: Route, interface: String, basePath: String): Int ⇒ Boolean =
    (port: Int) ⇒ {
      val mainRouter = pathPrefix(separateOnSlashes(basePath)) { route }

      val eventualBinding = Http().bindAndHandle(mainRouter, interface, port)
      Try(Await.result(eventualBinding, Duration(60, "seconds"))) match {
        case Failure(t) ⇒
          log.error("Error binding server", t.asInstanceOf[Exception])
          true
        case Success(_) ⇒
          log.info(
            s"""Server bound correctly
            Listening on port $port with binding $interface
            Try curl http://$interface:$port/$basePath/status"""
          )
          false
      }
    }

  bind(configureRoutes(aS), akkaConfig.getString("akka.http.interface"), akkaConfig.getString("akka.http.path-prefix"))(akkaConfig.getInt("akka.http.port"))

}
