package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{CsrfWrapper, OrderItem, OrderItemRepo}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreateOrderItemForm(quantity: Int, product: Long, order: Long)
case class UpdateOrderItemForm(id: Long, quantity: Int, product: Long, order: Long)

@Singleton
class OrderItemController @Inject()(orderItemRepo: OrderItemRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createOrderItemForm: Form[CreateOrderItemForm] = Form {
    mapping(
      "quantity" -> number,
      "product" -> longNumber,
      "order" -> longNumber,
    )(CreateOrderItemForm.apply)(CreateOrderItemForm.unapply)
  }
  val updateOrderItemForm: Form[UpdateOrderItemForm] = Form {
    mapping(
      "id" -> longNumber,
      "quantity" -> number,
      "product" -> longNumber,
      "order" -> longNumber,
    )(UpdateOrderItemForm.apply)(UpdateOrderItemForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    orderItemRepo.all().map(orderItems => render {
      case Accepts.Html() => Ok(views.html.ssr.orderItems.index(orderItems, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(orderItems))
    })
  })

  def _new: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.orderItems._new(createOrderItemForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createOrderItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.orderItems._new(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      orderItem => {
        orderItemRepo.create(orderItem.quantity, orderItem.product, orderItem.order).map { _ =>
          Redirect(controllers.ssr.routes.OrderItemController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    orderItemRepo.getById(id).map {
      case Some(orderItem) => Ok(views.html.ssr.orderItems.edit(id, updateOrderItemForm.fill(UpdateOrderItemForm(orderItem.id, orderItem.quantity, orderItem.product, orderItem.order))))
      case None => NotFound("OrderItem not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateOrderItemForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.orderItems.edit(id, errorForm)))
      },
      orderItemForm => {
        orderItemRepo.update(orderItemForm.id, OrderItem(orderItemForm.id, orderItemForm.quantity, orderItemForm.product, orderItemForm.order)).map { _ =>
          Redirect(controllers.ssr.routes.OrderItemController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    orderItemRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.OrderItemController.all)
    }
  }

}
