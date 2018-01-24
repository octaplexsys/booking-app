package com.maurogonzalez

import akka.http.scaladsl.model.StatusCodes
import com.maurogonzalez.models.Status
import com.maurogonzalez.services.StatusService
import io.circe.generic.auto._


class TestStatusService extends TestBaseService {

  "StatusService" when {
    "GET /status" should {
      "return time" in {
        Get("/status") ~> StatusService().routes() ~> check {
          status should be(StatusCodes.OK)
          responseAs[Status].uptime should include("milliseconds")
        }
      }
    }
  }

}
