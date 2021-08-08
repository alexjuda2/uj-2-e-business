package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{ProductPromotion, ProductPromotionRepo, CsrfWrapper}
import play.api.data.{Form}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.libs.json.{Json}
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreateProductPromotionForm(product: Long, promotion: Long)
case class UpdateProductPromotionForm(id: Long, product: Long, promotion: Long)

@Singleton
class ProductPromotionController @Inject()(productPromotionRepo: ProductPromotionRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createProductPromotionForm: Form[CreateProductPromotionForm] = Form {
    mapping(
      "product" -> longNumber,
      "promotion" -> longNumber,
    )(CreateProductPromotionForm.apply)(CreateProductPromotionForm.unapply)
  }
  val updateProductPromotionForm: Form[UpdateProductPromotionForm] = Form {
    mapping(
      "id" -> longNumber,
      "product" -> longNumber,
      "promotion" -> longNumber,
    )(UpdateProductPromotionForm.apply)(UpdateProductPromotionForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    productPromotionRepo.all().map(productPromotions => render {
      case Accepts.Html() => Ok(views.html.ssr.productPromotions.index(productPromotions, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(productPromotions))
    })
  })

  def _new: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.productPromotions._new(createProductPromotionForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createProductPromotionForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.productPromotions._new(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      productPromotion => {
        productPromotionRepo.create(productPromotion.product, productPromotion.promotion).map { _ =>
          Redirect(controllers.ssr.routes.ProductPromotionController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    productPromotionRepo.getById(id).map {
      case Some(productPromotion) => Ok(views.html.ssr.productPromotions.edit(id, updateProductPromotionForm.fill(UpdateProductPromotionForm(productPromotion.id, productPromotion.product, productPromotion.promotion))))
      case None => NotFound("ProductPromotion not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateProductPromotionForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.productPromotions.edit(id, errorForm)))
      },
      productPromotionForm => {
        productPromotionRepo.update(productPromotionForm.id, ProductPromotion(productPromotionForm.id, productPromotionForm.product, productPromotionForm.promotion)).map { _ =>
          Redirect(controllers.ssr.routes.ProductPromotionController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    productPromotionRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.ProductPromotionController.all)
    }
  }

}
