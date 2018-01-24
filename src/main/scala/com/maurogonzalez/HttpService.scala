package com.maurogonzalez

import akka.actor.ActorSystem
import akka.http.scaladsl.server._
import akka.stream.Materializer
import com.typesafe.config.Config

import scala.concurrent.ExecutionContextExecutor

trait HttpService extends RouteConcatenation {
  implicit val aS: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val aM: Materializer

  def akkaConfig: Config

  protected def configureRoutes(system: ActorSystem): Route
}
