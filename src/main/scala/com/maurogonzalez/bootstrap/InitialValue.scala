package com.maurogonzalez.bootstrap

import com.maurogonzalez.models.{Client, Commission, Organizer, PaymentMethod}

class InitialValue {

  val r = scala.util.Random

  val pms: List[PaymentMethod] = List(
    PaymentMethod(
      id = "cc",
      name = "Credit Card"
    ),
    PaymentMethod(
      id = "cash",
      name = "Cash"
    )
  )

  def cms: List[Commission] = pms.map( p => Commission(
    paymentMethodId = p.id,
    percentage = Some(r.nextFloat.toDouble)
  ))

  val organizers: List[Organizer] = List(
    Organizer(
      email = "bubba@bubba.com",
      org = "Bubba",
      commissions = Some(cms)
    ),
    Organizer(
      email = "forest@gump.com",
      org = "Gump",
      commissions = Some(cms)
    )
  )

  val clients: List[Client] = List(
    Client(
      email = "client1@bubba.com"
    ),
    Client(
      email = "client2@gump.com"
    )
  )
}
