package models

import play.api.libs.json.Json

case class OrderItem(id: Long, quantity: Int, product: Long, order: Long)

object OrderItem {
  implicit val format = Json.format[OrderItem]
}