package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.ProductRepo
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.ExecutionContext


@Singleton
class ProductController @Inject()(productRepo: ProductRepo, scc: DefaultSilhouetteControllerComponents)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getProducts: Action[AnyContent] = Action.async { implicit request =>
    val productsFuture = productRepo.all()
    productsFuture.map(products => render {
      case Accepts.Html() => Ok(views.html.products(products))
      case Accepts.Json() => Ok(Json.toJson(products))
    })
  }

  def getTopSecretProducts = silhouette.SecuredAction.async { implicit request =>
    val productsFuture = productRepo.all()
    productsFuture.map(products => render {
      case Accepts.Html() => Ok(views.html.products(products))
      case Accepts.Json() => Ok(Json.toJson(products))
    })
  }

}
