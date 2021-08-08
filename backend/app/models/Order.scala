package models

import play.api.libs.json.Json

case class Order(id: Long, address: String, user: Long)

object Order {
  implicit val format = Json.format[Order]
}