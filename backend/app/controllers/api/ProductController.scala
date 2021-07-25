package controllers.api

import javax.inject.{Inject, Singleton}
import models.ProductRepo
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.ExecutionContext


@Singleton
class ProductController @Inject()(productRepo: ProductRepo, cc: ControllerComponents)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def allProducts = Action.async {
    val productsFuture = productRepo.list()
    productsFuture.map(products => Ok(Json.toJson(products)))
  }
}
