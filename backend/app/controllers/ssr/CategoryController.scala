package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.CategoryRepo
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesActionBuilder}
import play.filters.csrf.CSRF

import scala.concurrent.{ExecutionContext, Future}

case class CreateCategoryForm(name: String)

@Singleton
class CategoryController @Inject()(categoryRepo: CategoryRepo, scc: DefaultSilhouetteControllerComponents, messagesActionBuilder: MessagesActionBuilder)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  val createCategoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }

  def all: Action[AnyContent] = Action.async { implicit request =>
    categoryRepo.all().map(categories => render {
      case Accepts.Html() => Ok(views.html.ssr.categories.index(categories))
      case Accepts.Json() => Ok(Json.toJson(categories))
    })
  }

  def _new: Action[AnyContent] = silhouette.SecuredAction { implicit request =>
    // This only makes sense for SSR page

    Ok(views.html.ssr.categories._new(createCategoryForm))
  }

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.ssr.categories._new(errorForm))
        )
      },
      category => {
        categoryRepo.create(category.name).map { _ =>
          Redirect(controllers.ssr.routes.CategoryController.all)
        }
      }
    )
  }

}
