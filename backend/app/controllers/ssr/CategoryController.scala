package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.CategoryRepo
import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.CSRF

import scala.concurrent.{ExecutionContext, Future}

case class CreateCategoryForm(name: String)

@Singleton
class CategoryController @Inject()(categoryRepo: CategoryRepo, scc: DefaultSilhouetteControllerComponents)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
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

  def _new: Action[AnyContent] = Action { implicit request =>
    // This only makes sense for SSR page

    val csrfToken = CSRF.getToken.get
    Ok(views.html.ssr.categories._new(csrfToken))
  }

  def create: Action[AnyContent] = Action.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    createCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(views.html.ssr.categories._new(csrfToken))
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
