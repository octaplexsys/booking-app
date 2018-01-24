package com.maurogonzalez.models

import io.swagger.annotations._

import scala.annotation.meta.field

@ApiModel(value="Name")
case class Name(
                   @(ApiModelProperty @field) name: String
                 )
