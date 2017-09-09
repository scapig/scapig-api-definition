package repository

import javax.inject.{Inject, Singleton}

import models.JsonFormatters._
import models.APIDefinition
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.UpdateWriteResult
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json.ImplicitBSONHandlers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class APIDefinitionRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi)  {

  val repository: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection[JSONCollection]("tapi-api-definition"))

  def save(apiDefinition: APIDefinition): Future[APIDefinition] = {
    repository.flatMap(collection =>
      collection.update(
        Json.obj("serviceName"-> apiDefinition.serviceName), apiDefinition, upsert = true) map {
        case result: UpdateWriteResult if result.ok => apiDefinition
        case error => throw new RuntimeException(s"Failed to save api-definition ${error.errmsg}")
      }
    )
  }

  def fetchByContext(context: String): Future[Option[APIDefinition]] = {
    repository.flatMap(collection =>
      collection.find(Json.obj("context"-> context)).one[APIDefinition]
    )
  }

}
