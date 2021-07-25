package models

import play.api.libs.json.Json

// TODO: add category field
case class Product(id: Long, name: String, description: String, category: Long)

object Product {
  implicit val productFormat = Json.format[Product]
}