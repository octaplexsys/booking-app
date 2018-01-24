package com.maurogonzalez.services

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.swagger.akka._
import com.github.swagger.akka.model.Info
import com.maurogonzalez.RequestLogging
import io.swagger.models.Scheme

import scala.reflect.runtime.{universe => ru}

case class SwaggerDocService(aM: ActorMaterializer, b: String, h: String, p: String)(implicit aS: ActorSystem) extends SwaggerHttpService with HasActorSystem with RequestLogging {
  override implicit val actorSystem: ActorSystem = aS
  override implicit val materializer: ActorMaterializer = aM
  override val apiTypes = Seq(
    ru.typeOf[StatusService],
    ru.typeOf[ClientService],
    ru.typeOf[OrganizerService],
    ru.typeOf[EventService],
    ru.typeOf[PaymentMethodService],
    ru.typeOf[BookingService]
  )
  override val basePath: String = b
  override val host = s"$h:$p"
  override val apiDocsPath = "api-docs"
  override val info = Info(description = "Sales App", version = "v1.0.0",
    title = "Sales App", termsOfService = "")
  override val scheme: Scheme = Scheme.HTTP
}