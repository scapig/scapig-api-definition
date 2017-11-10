package controllers

import javax.inject.{Inject, Singleton}

import models.JsonFormatters._
import models._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.APIDefinitionService

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class APIDefinitionController  @Inject()(cc: ControllerComponents,
                                         apiDefinitionService: APIDefinitionService) extends AbstractController(cc) with CommonControllers {

  def createOrUpdate(): Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[APIVersionRequest] { apiVersionRequest: APIVersionRequest =>

      apiDefinitionService.createOrUpdate(apiVersionRequest) map { api => Ok(Json.toJson(api))}
    }
  }

  def fetchByContext(context: String) = Action.async { implicit request =>
    apiDefinitionService.fetchByContext(context) map {
      case Some(api) => Ok(Json.toJson(api))
      case None => ApiNotFound(context).toHttpResponse
    }
  }

  def findAll() = Action.async { implicit request =>
    apiDefinitionService.findAll() map { apis => Ok(Json.toJson(apis))}
  }

}
