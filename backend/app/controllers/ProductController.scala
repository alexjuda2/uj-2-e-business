package controllers

import com.google.inject.Singleton
import models.SimpleProduct

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.{Json, OFormat}

import scala.collection.mutable

@Singleton
class ProductController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    val productList = new mutable.ListBuffer[SimpleProduct]()
    productList.append(new SimpleProduct("foo"))

    implicit val productListJson: OFormat[SimpleProduct] = Json.format[SimpleProduct]

    Ok(Json.toJson(productList))
  }

}
