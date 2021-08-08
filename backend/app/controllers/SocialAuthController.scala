package controllers

import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.impl.providers._
import models.SessionInfo
import play.api.libs.json.Json

import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, Cookie, DiscardingCookie, Request}
import play.filters.csrf.CSRF.Token
import play.filters.csrf.{CSRF, CSRFAddToken}

import scala.concurrent.{ExecutionContext, Future}

class SocialAuthController @Inject()(scc: DefaultSilhouetteControllerComponents, addToken: CSRFAddToken)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  def authenticate(provider: String): Action[AnyContent] = addToken(Action.async { implicit request: Request[AnyContent] =>
    val appUrl = "https://ashy-dune-057e0f003.azurestaticapps.net"
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            _ <- userRepository.create(profile.loginInfo.providerID, profile.loginInfo.providerKey, profile.email.getOrElse(""))
            _ <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- authenticatorService.create(profile.loginInfo)
            value <- authenticatorService.init(authenticator)
            result <- authenticatorService.embed(value, Redirect(appUrl))
          } yield {
            val Token(name, value) = CSRF.getToken.get
            result.withCookies(Cookie(name, value, httpOnly = false))
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        Forbidden("Forbidden" + e.getMessage())
    }
  })

  def signOut = silhouette.SecuredAction.async { implicit request =>
    authenticatorService.discard(request.authenticator, Redirect(controllers.ssr.routes.HomeController.index))
      .map(_.discardingCookies(
        DiscardingCookie(name = "csrfToken"),
        DiscardingCookie(name = "PLAY_SESSION"),
        DiscardingCookie(name = "OAuth2State")
      ))
  }

  def sessionInfo = silhouette.UserAwareAction { implicit request =>
    request.identity match {
      case Some(identity) => Ok(Json.toJson(SessionInfo(identity.id, identity.email)))
      case None => NotFound("No session found")
    }
  }
}