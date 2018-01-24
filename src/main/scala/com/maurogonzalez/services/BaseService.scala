package com.maurogonzalez.services

import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport

import scala.concurrent.ExecutionContext

trait BaseService extends FailFastCirceSupport {
  implicit def executor: ExecutionContext
  def log: LoggingAdapter
  def routes(): Route
}
