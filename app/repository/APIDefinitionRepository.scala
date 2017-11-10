package repository

import javax.inject.{Inject, Singleton}

import models.JsonFormatters._
import models.APIDefinition
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json.ImplicitBSONHandlers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class APIDefinitionRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi)  {

  def repository: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection[JSONCollection]("tapi-api-definition"))

  def save(apiDefinition: APIDefinition): Future[APIDefinition] = {
    repository.flatMap(collection =>
      collection.update(
        Json.obj("context"-> apiDefinition.context), apiDefinition, upsert = true) map {
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

  def findAll(): Future[Seq[APIDefinition]] = {
    repository.flatMap(collection =>
      collection.find(Json.obj()).cursor[APIDefinition]().collect[Seq]()
    )
  }

  private def createIndex(field: String, indexName: String): Future[WriteResult] = {
    repository.flatMap(collection =>
      collection.indexesManager.create(Index(Seq((field, IndexType.Ascending)), Some(indexName)))
    )
  }

  private def ensureIndexes() = {
    Future.sequence(Seq(
    createIndex("context", "contextIndex")
    ))
  }

  ensureIndexes()
}
