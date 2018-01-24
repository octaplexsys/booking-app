package com.maurogonzalez

import akka.event.Logging
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.RouteResult
import akka.http.scaladsl.server.directives.LoggingMagnet

trait RequestLogging {
  protected val service = "sales-sample-app"

  protected val loggingMagnet: LoggingMagnet[(HttpRequest) => (RouteResult) => Unit] =
    LoggingMagnet.forRequestResponseFromMarkerAndLevel((service, Logging.InfoLevel))
}
