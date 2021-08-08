package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{CsrfWrapper, Product, ProductRepo}
import play.api.data.Form
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ProductController @Inject()(productRepo: ProductRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  def getTopSecretProducts = silhouette.SecuredAction.async { implicit request =>
    val productsFuture = productRepo.all()
    productsFuture.map(products => Ok(Json.toJson(products)))
  }

  implicit val formErrWrites = FormErrorWrites

  val createProductForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber,
      "currency" -> longNumber,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  val updateProductForm: Form[UpdateProductForm] = Form {
    mapping(
      "id" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber,
      "currency" -> longNumber,
    )(UpdateProductForm.apply)(UpdateProductForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    productRepo.all().map(products => render {
      case Accepts.Html() => Ok(views.html.ssr.products.index(products, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(products))
    })
  })

  def getNew: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.products.getNew(createProductForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.products.getNew(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      product => {
        productRepo.create(product.name, product.description, product.category, product.currency).map { _ =>
          Redirect(controllers.ssr.routes.ProductController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    productRepo.getById(id).map {
      case Some(product) => Ok(views.html.ssr.products.edit(id, updateProductForm.fill(UpdateProductForm(product.id, product.name, product.description, product.category, product.currency))))
      case None => NotFound("Product not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateProductForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.products.edit(id, errorForm)))
      },
      productForm => {
        productRepo.update(productForm.id, Product(productForm.id, productForm.name, productForm.description, productForm.category, productForm.currency)).map { _ =>
          Redirect(controllers.ssr.routes.ProductController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    productRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.ProductController.all)
    }
  }

}

case class CreateProductForm(name: String, description: String, category: Long, currency: Long)
case class UpdateProductForm(id: Long, name: String, description: String, category: Long, currency: Long)