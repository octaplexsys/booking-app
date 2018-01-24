package com.maurogonzalez.models

case class Commission(
                       paymentMethodId: String,
                       percentage: Option[BigDecimal] = None,
                       value: Option[BigDecimal] = None
                     )
