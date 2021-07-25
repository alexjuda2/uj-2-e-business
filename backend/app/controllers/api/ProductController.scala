package controllers.api

import javax.inject.{Inject, Singleton}
import models.{Product, ProductRepo}
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class ProductController @Inject()(productRepo: ProductRepo, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

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

  def createProduct = Action(parse.json).async { request =>
    val productResult = request.body.validate[Product]
    productResult.fold(
      errors => {
        Future.successful(
          BadRequest(Json.obj("message" -> JsError.toJson(errors)))
        )
      },
      product => {
        productRepo.create(product.name, product.description).map { insertedProduct =>
          Ok(Json.toJson(insertedProduct))
        }
      }
    )
  }
}
