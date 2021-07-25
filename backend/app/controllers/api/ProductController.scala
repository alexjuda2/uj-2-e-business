package controllers.api

import javax.inject.{Inject, Singleton}
import models.ProductRepo
import play.api.data.{Form, FormError}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText}
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ProductController @Inject()(productRepo: ProductRepo, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {
  // Allows us to return form errors as JSON
  implicit val formErrorWrites = new Writes[FormError] {
    def writes(o: FormError): JsValue = Json.obj(
      "key" -> Json.toJson(o.key),
      "messages" -> Json.toJson(o.messages),
    )
  }

  val productForm: Form[CreateProductForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "category" -> longNumber,
    )(CreateProductForm.apply)(CreateProductForm.unapply)
  }

  def allProducts = Action.async {
    val productsFuture = productRepo.all()
    productsFuture.map(products => Ok(Json.toJson(products)))
  }

  def showProduct(id: Long) = Action.async {
    val productFuture = productRepo.getById(id)
    productFuture.map(product => product match {
      case Some(p) => Ok(Json.toJson(p))
      case None => NotFound(Json.toJson(Map("message" -> "Product not found")))
    })
  }

  def createProduct = Action(parse.json).async { implicit request =>
    productForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          BadRequest(Json.obj("errors" -> Json.toJson(errorForm.errors)))
        )
      },
      product => {
        productRepo.create(product.name, product.description, product.category).map { insertedProduct =>
          Ok(Json.toJson(insertedProduct))
        }
      }
    )
  }
}

case class CreateProductForm(name: String, description: String, category: Long)