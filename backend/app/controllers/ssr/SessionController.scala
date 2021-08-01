package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import play.api.mvc.DiscardingCookie

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class SessionController @Inject()(scc: DefaultSilhouetteControllerComponents)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  def signOut = silhouette.SecuredAction.async { implicit request =>
    authenticatorService.discard(request.authenticator, Ok("Signed out"))
      .map(_.discardingCookies(
        DiscardingCookie(name = "csrfToken"),
        DiscardingCookie(name = "PLAY_SESSION"),
        DiscardingCookie(name = "OAuth2State")
      ))
  }
}
