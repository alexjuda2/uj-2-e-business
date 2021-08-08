package models

import play.api.libs.json.Json

case class Review(id: Long, content: String, rating: Int, user: Long)

object Review {
  implicit val format = Json.format[Review]
}