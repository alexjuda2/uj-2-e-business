package controllers.ssr

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import play.api.mvc.{Action, AnyContent, DiscardingCookie}
import play.filters.csrf.CSRFAddToken

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class SessionController @Inject()(scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  def signOut = silhouette.SecuredAction { implicit request =>
//  def signOut: Action[AnyContent] = securedAction { implicit request: SecuredRequest[EnvType, AnyContent] =>
    Ok("yasss")
//    authenticatorService.discard(request.authenticator, Ok("Signed out"))
//      .map(_.discardingCookies(
//        DiscardingCookie(name = "csrfToken"),
//        DiscardingCookie(name = "PLAY_SESSION"),
//        DiscardingCookie(name = "OAuth2State")
//      ))
  }
}
