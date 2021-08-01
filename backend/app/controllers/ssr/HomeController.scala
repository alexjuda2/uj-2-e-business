package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}
import play.api.mvc.{Action, AnyContent}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class HomeController @Inject()(scc: DefaultSilhouetteControllerComponents)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {

  def index = silhouette.UserAwareAction { implicit request =>
    request.identity match {
      case Some(identity) => Ok(views.html.ssr.home.index(identity.email))
      case None => Ok(views.html.ssr.home.index("Noone, really"))
    }
  }

}
