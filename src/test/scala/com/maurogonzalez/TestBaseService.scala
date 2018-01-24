package com.maurogonzalez

import akka.event.{LoggingAdapter, NoLogging}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, WordSpec}

trait TestBaseService extends  WordSpec with Matchers with ScalatestRouteTest with MockFactory with FailFastCirceSupport {
  protected def log: LoggingAdapter = NoLogging
}