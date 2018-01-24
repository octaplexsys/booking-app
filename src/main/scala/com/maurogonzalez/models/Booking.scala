package com.maurogonzalez.models


case class Booking(
                  id: Option[String],
                  client: String,
                  eventName: String,
                  price: Option[BigDecimal],
                  qty: Int,
                  subTotal: BigDecimal = 0,
                  commission: BigDecimal = 0,
                  total: BigDecimal = 0,
                  paymentMethodId: String
                  )