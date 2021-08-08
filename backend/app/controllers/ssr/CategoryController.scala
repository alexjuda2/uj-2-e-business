package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{Category, CategoryRepo, CsrfWrapper}
import play.api.data.{Form}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.libs.json.{Json}
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreateCategoryForm(name: String)
case class UpdateCategoryForm(id: Long, name: String)

@Singleton
class CategoryController @Inject()(categoryRepo: CategoryRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createCategoryForm: Form[CreateCategoryForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply)
  }
  val updateCategoryForm: Form[UpdateCategoryForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdateCategoryForm.apply)(UpdateCategoryForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    categoryRepo.all().map(categories => render {
      case Accepts.Html() => Ok(views.html.ssr.categories.index(categories, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(categories))
    })
  })

  def getNew: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.categories.getNew(createCategoryForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.categories.getNew(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      category => {
        categoryRepo.create(category.name).map { _ =>
          Redirect(controllers.ssr.routes.CategoryController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    val category = categoryRepo.getById(id)
    category.map {
      case Some(cat) => Ok(views.html.ssr.categories.edit(id, updateCategoryForm.fill(UpdateCategoryForm(cat.id, cat.name))))
      case None => NotFound("Category not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateCategoryForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.categories.edit(id, errorForm)))
      },
      categoryForm => {
        categoryRepo.update(categoryForm.id, Category(categoryForm.id, categoryForm.name)).map { _ =>
          Redirect(controllers.ssr.routes.CategoryController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    categoryRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.CategoryController.all)
    }
  }

}
