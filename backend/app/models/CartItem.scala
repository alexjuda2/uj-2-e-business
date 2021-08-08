package models

import play.api.libs.json.Json

case class CartItem(id: Long, quantity: Int, product: Long, user: Long)

object CartItem {
  implicit val format = Json.format[CartItem]
}