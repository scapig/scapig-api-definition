package services

import javax.inject.{Inject, Singleton}

import models.{APIDefinition, ContextAlreadyDefinedForAnotherService, ContextAlreadyDefinedForAnotherServiceException}
import repository.APIDefinitionRepository
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@Singleton
class APIDefinitionService @Inject()(apiDefinitionRepository: APIDefinitionRepository) {

  def createOrUpdate(apiDefinition: APIDefinition): Future[APIDefinition] = {
    for {
      existingApi <- apiDefinitionRepository.fetchByContext(apiDefinition.context)
      _ = if(existingApi.exists(api => api.serviceName != apiDefinition.serviceName)) throw ContextAlreadyDefinedForAnotherServiceException(apiDefinition.context, apiDefinition.serviceName)
      apiDefinition <- apiDefinitionRepository.save(apiDefinition)
    } yield apiDefinition
  }

}
