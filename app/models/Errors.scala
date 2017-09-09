package models

import play.api.http.Status._
import play.api.libs.json.Json
import play.api.mvc.{Result, Results}
import models.JsonFormatters._

sealed abstract class ErrorResponse(
                                     val httpStatusCode: Int,
                                     val errorCode: String,
                                     val message: String) {

  def toHttpResponse: Result = Results.Status(httpStatusCode)(Json.toJson(this))
}

case class ErrorInvalidRequest(errorMessage: String) extends ErrorResponse(BAD_REQUEST, "INVALID_REQUEST", errorMessage)
case object ContextAlreadyDefinedForAnotherService extends ErrorResponse(CONFLICT, "CONTEXT_ALREADY_DEFINED", "Context is already defined for another service. It must be unique per service.")
case class ApiNotFound(context: String) extends ErrorResponse(NOT_FOUND, "NOT_FOUND", s"no api found for context $context")

class ValidationException(message: String) extends RuntimeException(message)
case class ContextAlreadyDefinedForAnotherServiceException(context: String, serviceName: String) extends RuntimeException(s"Context '$context' was already defined for service '$serviceName'")
