package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{Promotion, PromotionRepo, CsrfWrapper}
import play.api.data.{Form}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.libs.json.{Json}
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreatePromotionForm(name: String)
case class UpdatePromotionForm(id: Long, name: String)

@Singleton
class PromotionController @Inject()(promotionRepo: PromotionRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createPromotionForm: Form[CreatePromotionForm] = Form {
    mapping(
      "name" -> nonEmptyText,
    )(CreatePromotionForm.apply)(CreatePromotionForm.unapply)
  }
  val updatePromotionForm: Form[UpdatePromotionForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
    )(UpdatePromotionForm.apply)(UpdatePromotionForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    promotionRepo.all().map(promotions => render {
      case Accepts.Html() => Ok(views.html.ssr.promotions.index(promotions, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(promotions))
    })
  })

  def getNew: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.promotions.getNew(createPromotionForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createPromotionForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.promotions.getNew(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      promotion => {
        promotionRepo.create(promotion.name).map { _ =>
          Redirect(controllers.ssr.routes.PromotionController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    promotionRepo.getById(id).map {
      case Some(promotion) => Ok(views.html.ssr.promotions.edit(id, updatePromotionForm.fill(UpdatePromotionForm(promotion.id, promotion.name))))
      case None => NotFound("Promotion not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updatePromotionForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.promotions.edit(id, errorForm)))
      },
      promotionForm => {
        promotionRepo.update(promotionForm.id, Promotion(promotionForm.id, promotionForm.name)).map { _ =>
          Redirect(controllers.ssr.routes.PromotionController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    promotionRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.PromotionController.all)
    }
  }

}
