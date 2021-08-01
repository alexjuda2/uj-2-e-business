package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import play.filters.csrf.{CSRF, CSRFAddToken}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {

  def index = addToken(silhouette.UserAwareAction { implicit request =>
    val csrfToken = CSRF.getToken.get
    request.identity match {
      case Some(identity) => Ok(views.html.ssr.home.index(identity.email, csrfToken))
      case None => Ok(views.html.ssr.home.index("Noone, really", csrfToken))
    }
  })

  def topSecret = silhouette.SecuredAction { implicit request =>
    Ok(views.html.ssr.home.topSecret())
  }

}
