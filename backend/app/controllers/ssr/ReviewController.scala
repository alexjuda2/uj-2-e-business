package controllers.ssr

import controllers.{AbstractAuthController, DefaultSilhouetteControllerComponents}

import javax.inject.{Inject, Singleton}
import models.{CsrfWrapper, Review, ReviewRepo}
import play.api.data.{Form, Forms}
import play.api.data.Forms.{longNumber, mapping, nonEmptyText, number}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import play.filters.csrf.{CSRF, CSRFAddToken}
import utils.FormErrorWrites

import scala.concurrent.{ExecutionContext, Future}

case class CreateReviewForm(content: String, rating: Int, user: Long)
case class UpdateReviewForm(id: Long, content: String, rating: Int, user: Long)

@Singleton
class ReviewController @Inject()(reviewRepo: ReviewRepo, scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends AbstractAuthController(scc) {
  implicit val formErrWrites = FormErrorWrites

  val createReviewForm: Form[CreateReviewForm] = Form {
    mapping(
      "content" -> Forms.text,
      "rating" -> number,
      "user" -> longNumber,
    )(CreateReviewForm.apply)(CreateReviewForm.unapply)
  }
  val updateReviewForm: Form[UpdateReviewForm] = Form {
    mapping(
      "id" -> longNumber,
      "content" -> Forms.text,
      "rating" -> number,
      "user" -> longNumber,
    )(UpdateReviewForm.apply)(UpdateReviewForm.unapply)
  }

  def all: Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    val csrfToken = CSRF.getToken.get
    reviewRepo.all().map(reviews => render {
      case Accepts.Html() => Ok(views.html.ssr.reviews.index(reviews, csrfToken))
      case Accepts.Json() => Ok(Json.toJson(reviews))
    })
  })

  def getNew: Action[AnyContent] = addToken(silhouette.SecuredAction { implicit request =>
    render {
      case Accepts.Html() => Ok(views.html.ssr.reviews.getNew(createReviewForm))
      case Accepts.Json() => Ok(Json.toJson(CsrfWrapper(play.filters.csrf.CSRF.getToken.get.value)))
    }
  })

  def create: Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    createReviewForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(
          render {
            case Accepts.Html() => BadRequest(views.html.ssr.reviews.getNew(errorForm))
            case Accepts.Json() => BadRequest(Json.toJson(errorForm.errors))
          }

        )
      },
      review => {
        reviewRepo.create(review.content, review.rating, review.user).map { _ =>
          Redirect(controllers.ssr.routes.ReviewController.all)
        }
      }
    )
  }

  def edit(id: Long): Action[AnyContent] = addToken(silhouette.SecuredAction.async { implicit request =>
    // This only makes sense for SSR page

    reviewRepo.getById(id).map {
      case Some(review) => Ok(views.html.ssr.reviews.edit(id, updateReviewForm.fill(UpdateReviewForm(review.id, review.content, review.rating, review.user))))
      case None => NotFound("Review not found.")
    }
  })

  def update(id: Long): Action[AnyContent] = silhouette.SecuredAction.async { implicit request =>
    updateReviewForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(BadRequest(views.html.ssr.reviews.edit(id, errorForm)))
      },
      reviewForm => {
        reviewRepo.update(reviewForm.id, Review(reviewForm.id, reviewForm.content, reviewForm.rating, reviewForm.user)).map { _ =>
          Redirect(controllers.ssr.routes.ReviewController.all)
        }
      }
    )
  }

  def delete(id: Long) = silhouette.SecuredAction.async { implicit request =>
    reviewRepo.delete(id).map { _ =>
      Redirect(controllers.ssr.routes.ReviewController.all)
    }
  }

}
