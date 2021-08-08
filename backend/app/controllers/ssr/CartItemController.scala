package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{CartItem, CartItemRepo, CsrfWrapper}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreateCartItemForm(quantity: Int, product: Long, user: Long)
case class UpdateCartItemForm(id: Long, quantity: Int, product: Long, user: Long)

@Singleton
class CartItemController @Inject()(cartItemRepo: CartItemRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createCartItemForm: Form[CreateCartItemForm] = Form {
    mapping(
      "quantity" -> number,
      "product" -> longNumber,
      "user" -> longNumber,
    )(CreateCartItemForm.apply)(CreateCartItemForm.unapply)
  }
  val updateCartItemForm: Form[UpdateCartItemForm] = Form {
    mapping(
      "id" -> longNumber,
      "quantity" -> number,
      "product" -> longNumber,
      "user" -> longNumber,
    )(UpdateCartItemForm.apply)(UpdateCartItemForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    cartItemRepo.all().map(cartItems => render {
      case Accepts.Html() => Ok(views.html.ssr.cartItems.index(cartItems, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(cartItems))
    })
  })

  def _new: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.cartItems._new(createCartItemForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createCartItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.cartItems._new(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      cartItem => {
        cartItemRepo.create(cartItem.quantity, cartItem.product, cartItem.user).map { _ =>
          Redirect(controllers.ssr.routes.CartItemController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    cartItemRepo.getById(id).map {
      case Some(cartItem) => Ok(views.html.ssr.cartItems.edit(id, updateCartItemForm.fill(UpdateCartItemForm(cartItem.id, cartItem.quantity, cartItem.product, cartItem.user))))
      case None => NotFound("CartItem not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateCartItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.cartItems.edit(id, errorForm)))
      },
      cartItemForm => {
        cartItemRepo.update(cartItemForm.id, CartItem(cartItemForm.id, cartItemForm.quantity, cartItemForm.product, cartItemForm.user)).map { _ =>
          Redirect(controllers.ssr.routes.CartItemController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    cartItemRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.CartItemController.all)
    }
  }

}
