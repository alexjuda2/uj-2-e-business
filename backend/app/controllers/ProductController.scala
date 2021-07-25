package controllers

import javax.inject.{Inject, Singleton}
import models.{Product, ProductRepo, SimpleProduct}
// TODO: import play.api.data.{Form, Forms._}
import play.api.mvc.{AbstractController, ControllerComponents, MessagesControllerComponents, MessagesAbstractController, Action, AnyContent}
import scala.concurrent.ExecutionContext


@Singleton
class ProductController @Inject()(productRepo: ProductRepo, cc: MessagesControllerComponents)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc) {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def getProducts: Action[AnyContent] = Action.async { implicit request =>
    val productsFuture = productRepo.all()
    productsFuture.map(products => Ok(views.html.products(products)))
  }

}
