package utils

import play.api.data.FormError
import play.api.libs.json.{JsValue, Json, Writes}

object FormErrorWrites extends Writes[FormError] {
  override def writes(o: FormError): JsValue = Json.obj(
    "key" -> Json.toJson(o.key),
    "message" -> Json.toJson(o.message)
  )
}