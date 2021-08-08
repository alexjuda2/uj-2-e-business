package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{Currency, CurrencyRepo, CsrfWrapper}
import play.api.data.{Form}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.libs.json.{Json}
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreateCurrencyForm(code: String, symbol: String)
case class UpdateCurrencyForm(id: Long, code: String, symbol: String)

@Singleton
class CurrencyController @Inject()(currencyRepo: CurrencyRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createCurrencyForm: Form[CreateCurrencyForm] = Form {
    mapping(
      "code" -> nonEmptyText,
      "symbol" -> nonEmptyText,
    )(CreateCurrencyForm.apply)(CreateCurrencyForm.unapply)
  }
  val updateCurrencyForm: Form[UpdateCurrencyForm] = Form {
    mapping(
      "id" -> longNumber,
      "code" -> nonEmptyText,
      "symbol" -> nonEmptyText,
    )(UpdateCurrencyForm.apply)(UpdateCurrencyForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    currencyRepo.all().map(currencies => render {
      case Accepts.Html() => Ok(views.html.ssr.currencies.index(currencies, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(currencies))
    })
  })

  def getNew: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.currencies.getNew(createCurrencyForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createCurrencyForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.currencies.getNew(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      currency => {
        currencyRepo.create(currency.code, currency.symbol).map { _ =>
          Redirect(controllers.ssr.routes.CurrencyController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    currencyRepo.getById(id).map {
      case Some(currency) => Ok(views.html.ssr.currencies.edit(id, updateCurrencyForm.fill(UpdateCurrencyForm(currency.id, currency.code, currency.symbol))))
      case None => NotFound("Currency not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateCurrencyForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.currencies.edit(id, errorForm)))
      },
      currencyForm => {
        currencyRepo.update(currencyForm.id, Currency(currencyForm.id, currencyForm.code, currencyForm.symbol)).map { _ =>
          Redirect(controllers.ssr.routes.CurrencyController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    currencyRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.CurrencyController.all)
    }
  }

}
