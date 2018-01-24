package com.maurogonzalez.models

import io.swagger.annotations._
import scala.annotation.meta.field

@ApiModel(value="Status")
case class Status(
                   @(ApiModelProperty @field) uptime: String
                 )
