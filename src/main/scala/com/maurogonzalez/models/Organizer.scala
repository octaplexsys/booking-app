package com.maurogonzalez.models

case class Organizer(
                      email: String,
                      org: String,
                      commissions: Option[List[Commission]] = None
                    )
