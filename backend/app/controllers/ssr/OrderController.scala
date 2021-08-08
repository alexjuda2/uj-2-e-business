package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{Order, OrderRepo, CsrfWrapper}
import play.api.data.{Form}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.libs.json.{Json}
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreateOrderForm(address: String, user: Long)
case class UpdateOrderForm(id: Long, address: String, user: Long)

@Singleton
class OrderController @Inject()(orderRepo: OrderRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createOrderForm: Form[CreateOrderForm] = Form {
    mapping(
      "address" -> nonEmptyText,
      "user" -> longNumber,
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }
  val updateOrderForm: Form[UpdateOrderForm] = Form {
    mapping(
      "id" -> longNumber,
      "address" -> nonEmptyText,
      "user" -> longNumber,
    )(UpdateOrderForm.apply)(UpdateOrderForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    orderRepo.all().map(orders => render {
      case Accepts.Html() => Ok(views.html.ssr.orders.index(orders, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(orders))
    })
  })

  def getNew: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.orders.getNew(createOrderForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.orders.getNew(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      order => {
        orderRepo.create(order.address, order.user).map { _ =>
          Redirect(controllers.ssr.routes.OrderController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    orderRepo.getById(id).map {
      case Some(order) => Ok(views.html.ssr.orders.edit(id, updateOrderForm.fill(UpdateOrderForm(order.id, order.address, order.user))))
      case None => NotFound("Order not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateOrderForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.orders.edit(id, errorForm)))
      },
      orderForm => {
        orderRepo.update(orderForm.id, Order(orderForm.id, orderForm.address, orderForm.user)).map { _ =>
          Redirect(controllers.ssr.routes.OrderController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    orderRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.OrderController.all)
    }
  }

}
