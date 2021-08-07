package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{CategoryRepo, ProductRepo}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext


@Singleton
class CategoryController @Inject()(categoryRepo: CategoryRepo, scc: DefaultSilhouetteControllerComponents)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  def getAll: Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.all().map(categories => render {
      case Accepts.Html() => Ok(views.html.ssr.categories.index(categories))
      case Accepts.Json() => Ok(Json.toJson(categories))
    })
  }

  def _new: Action[AnyContent] = Action { implicit request =>
    // This only makes sense for SSR page
    Ok(views.html.ssr.categories.edit(Option.empty))
  }
}
