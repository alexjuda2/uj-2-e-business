package models

import play.api.libs.json.Json

case class ProductPromotion(id: Long, product: Long, promotion: Long)

object ProductPromotion {
  implicit val format = Json.format[ProductPromotion]
}