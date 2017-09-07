package services

import javax.inject.Singleton

import models.APIDefinition

import scala.concurrent.Future

@Singleton
class APIDefinitionService {

  def createOrUpdate(apiDefinition: APIDefinition): Future[APIDefinition] = {
    ???
  }
}
