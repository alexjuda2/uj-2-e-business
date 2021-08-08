package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{WishListItem, WishListItemRepo, CsrfWrapper}
import play.api.data.{Form}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.libs.json.{Json}
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreateWishListItemForm(product: Long, wishList: Long)
case class UpdateWishListItemForm(id: Long, product: Long, wishList: Long)

@Singleton
class WishListItemController @Inject()(wishListItemRepo: WishListItemRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createWishListItemForm: Form[CreateWishListItemForm] = Form {
    mapping(
      "product" -> longNumber,
      "wishList" -> longNumber,
    )(CreateWishListItemForm.apply)(CreateWishListItemForm.unapply)
  }
  val updateWishListItemForm: Form[UpdateWishListItemForm] = Form {
    mapping(
      "id" -> longNumber,
      "product" -> longNumber,
      "wishList" -> longNumber,
    )(UpdateWishListItemForm.apply)(UpdateWishListItemForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    wishListItemRepo.all().map(wishListItems => render {
      case Accepts.Html() => Ok(views.html.ssr.wishListItems.index(wishListItems, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(wishListItems))
    })
  })

  def _new: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.wishListItems._new(createWishListItemForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createWishListItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.wishListItems._new(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      wishListItem => {
        wishListItemRepo.create(wishListItem.product, wishListItem.wishList).map { _ =>
          Redirect(controllers.ssr.routes.WishListItemController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    wishListItemRepo.getById(id).map {
      case Some(wishListItem) => Ok(views.html.ssr.wishListItems.edit(id, updateWishListItemForm.fill(UpdateWishListItemForm(wishListItem.id, wishListItem.product, wishListItem.wishList))))
      case None => NotFound("WishListItem not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateWishListItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.wishListItems.edit(id, errorForm)))
      },
      wishListItemForm => {
        wishListItemRepo.update(wishListItemForm.id, WishListItem(wishListItemForm.id, wishListItemForm.product, wishListItemForm.wishList)).map { _ =>
          Redirect(controllers.ssr.routes.WishListItemController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    wishListItemRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.WishListItemController.all)
    }
  }

}
