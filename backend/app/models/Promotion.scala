package models

import play.api.libs.json.Json

case class Promotion(id: Long, name: String)

object Promotion {
  implicit val format = Json.format[Promotion]
}
