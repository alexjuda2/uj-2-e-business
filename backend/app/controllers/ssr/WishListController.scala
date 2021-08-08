package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{WishList, WishListRepo, CsrfWrapper}
import play.api.data.{Form}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.libs.json.{Json}
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreateWishListForm(name: String, user: Long)
case class UpdateWishListForm(id: Long, name: String, user: Long)

@Singleton
class WishListController @Inject()(wishListRepo: WishListRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createWishListForm: Form[CreateWishListForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "user" -> longNumber,
    )(CreateWishListForm.apply)(CreateWishListForm.unapply)
  }
  val updateWishListForm: Form[UpdateWishListForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "user" -> longNumber,
    )(UpdateWishListForm.apply)(UpdateWishListForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    wishListRepo.all().map(wishLists => render {
      case Accepts.Html() => Ok(views.html.ssr.wishLists.index(wishLists, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(wishLists))
    })
  })

  def _new: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.wishLists._new(createWishListForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createWishListForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.wishLists._new(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      wishList => {
        wishListRepo.create(wishList.name, wishList.user).map { _ =>
          Redirect(controllers.ssr.routes.WishListController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    wishListRepo.getById(id).map {
      case Some(wishList) => Ok(views.html.ssr.wishLists.edit(id, updateWishListForm.fill(UpdateWishListForm(wishList.id, wishList.name, wishList.user))))
      case None => NotFound("WishList not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateWishListForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.wishLists.edit(id, errorForm)))
      },
      wishListForm => {
        wishListRepo.update(wishListForm.id, WishList(wishListForm.id, wishListForm.name, wishListForm.user)).map { _ =>
          Redirect(controllers.ssr.routes.WishListController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    wishListRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.WishListController.all)
    }
  }

}
