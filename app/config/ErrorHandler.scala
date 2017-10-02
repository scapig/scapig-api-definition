package config

import javax.inject.Singleton

import models.ErrorInvalidRequest
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Result, Results}
import models.JsonFormatters._

import scala.concurrent.Future

@Singleton
class ErrorHandler extends DefaultHttpErrorHandler {
  override def onBadRequest(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(Results.BadRequest(Json.toJson(ErrorInvalidRequest(message)).toString()))
  }
}