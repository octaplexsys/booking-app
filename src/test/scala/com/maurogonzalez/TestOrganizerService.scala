package com.maurogonzalez

import com.maurogonzalez.models.Organizer
import com.maurogonzalez.services.OrganizerService
import io.circe.generic.auto._
import scala.collection.mutable

class TestOrganizerService extends TestBaseService {

  val osSize = 10
  val r: Range.Inclusive = 1 to osSize
  var os: mutable.HashMap[String, Organizer] = new mutable.HashMap()

  r foreach (i => {
    val e = s"test$i@example.com"
    os += (e -> Organizer(email = e, org = s"Test $i"))
  })

  "ListOrganizers" when {
    "GET /organizers" should {
      s"return $osSize organizers" in {
        Get("/organizers") ~> OrganizerService(os).routes() ~> check {
          responseAs[List[Organizer]]
            .length should be(10)
        }
      }
    }
  }
}
