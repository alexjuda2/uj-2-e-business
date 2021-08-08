package models

import play.api.libs.json.Json

case class WishListItem(id: Long, product: Long, wishList: Long)

object WishListItem {
  implicit val format = Json.format[WishListItem]
}