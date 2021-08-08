package models

import play.api.libs.json.Json

case class SessionInfo(userId: Long, userEmail: String)

object SessionInfo {
  implicit val format = Json.format[SessionInfo]
}