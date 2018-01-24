package com.maurogonzalez.models

case class Event(
                  name: String,
                  organizerEmail: String,
                  price: Option[BigDecimal],
                  commissions: Option[List[Commission]] = None
                )
