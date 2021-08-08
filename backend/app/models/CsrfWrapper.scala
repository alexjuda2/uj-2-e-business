package models

import play.api.libs.json.Json

case class CsrfWrapper(token: String)

object CsrfWrapper {
  implicit val csrfWrapperFormat = Json.format[CsrfWrapper]
}
