package com.maurogonzalez

import com.maurogonzalez.models.Client
import com.maurogonzalez.services.ClientService
import io.circe.generic.auto._
import scala.collection.mutable

class TestClientService extends TestBaseService {

  val osSize = 10
  val r: Range.Inclusive = 1 to osSize
  var cs: mutable.HashMap[String, Client] = new mutable.HashMap()

  r foreach (i => {
    val e = s"test$i@example.com"
    cs += (e -> Client(email = e))
  })

  "ClientService" when {

    "GET /clients" should {
      s"return $osSize clients" in {
        Get("/clients") ~> ClientService(cs).routes() ~> check {
          responseAs[List[Client]]
            .length should be(10)
        }
      }
    }

    "POST /clients" should {
      s"return succesor client" in {
        Post("/clients", Client(email = s"test${osSize +1}@example.com")) ~> ClientService(cs).routes() ~> check {
          responseAs[Client]
            .email should be(s"test${osSize +1}@example.com")
        }
      }
      s"return ${osSize +1} clients" in {
        Get("/clients") ~> ClientService(cs).routes() ~> check {
          responseAs[List[Client]]
            .length should be (11)
        }
      }
    }
  }
}
