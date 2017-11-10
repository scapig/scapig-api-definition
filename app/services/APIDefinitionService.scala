package services

import javax.inject.{Inject, Singleton}

import models.{APIDefinition, APIVersion, APIVersionRequest}
import repository.APIDefinitionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class APIDefinitionService @Inject()(apiDefinitionRepository: APIDefinitionRepository) {

  def fetchByContext(apiContext: String): Future[Option[APIDefinition]] = {
    apiDefinitionRepository.fetchByContext(apiContext)
  }

  def findAll(): Future[Seq[APIDefinition]] = {
    apiDefinitionRepository.findAll()
  }

  def createOrUpdate(apiVersionRequest: APIVersionRequest): Future[APIDefinition] = {
    for {
      newApiDefinition <- updatedApiDefinition(apiVersionRequest)
      apiDefinition <- apiDefinitionRepository.save(newApiDefinition)
    } yield apiDefinition
  }

  private def updatedApiDefinition(apiVersionRequest: APIVersionRequest): Future[APIDefinition] = {
    val apiVersion = APIVersion(
      apiVersionRequest.version,
      apiVersionRequest.serviceBaseUrl,
      apiVersionRequest.status,
      apiVersionRequest.endpoints)

    apiDefinitionRepository.fetchByContext(apiVersionRequest.context) map {
      case Some(apiDefinition) =>
        apiDefinition.copy(
          name = apiVersionRequest.apiName,
          description = apiVersionRequest.apiDescription,
          versions = apiDefinition.versions.filterNot(_.version == apiVersionRequest.version) :+ apiVersion
        )
      case None =>
        APIDefinition(apiVersionRequest.apiName, apiVersionRequest.apiDescription, apiVersionRequest.context,
          Seq(apiVersion))
    }
  }
}
