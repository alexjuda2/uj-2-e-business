package models

import play.api.libs.json.Json

case class Currency(id: Long, code: String, symbol: String)

object Currency {
  implicit val currencyFormat = Json.format[Currency]
}