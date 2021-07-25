package controllers.api

import javax.inject.{Inject, Singleton}
import models.ProductRepo
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext


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
}
