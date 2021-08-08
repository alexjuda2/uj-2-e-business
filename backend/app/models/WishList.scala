package models

import play.api.libs.json.Json

case class WishList(id: Long, name: String, user: Long)

object WishList {
  implicit val format = Json.format[WishList]
}