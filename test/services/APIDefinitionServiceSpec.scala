package services

import models._
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import repository.APIDefinitionRepository
import utils.UnitSpec

import scala.concurrent.Future
import scala.concurrent.Future.{failed, successful}

class APIDefinitionServiceSpec extends UnitSpec with MockitoSugar {

  val apiEndpoint = Endpoint("/today", "Get Today's Date", HttpMethod.GET, AuthType.NONE)
  val apiVersion = APIVersion("1.0", APIStatus.PROTOTYPED, Seq(apiEndpoint))
  val apiDefinition = APIDefinition("calendar", "/", "Calendar API", "My Calendar API", "calendar", Seq(apiVersion))

  trait Setup {
    val mockApiDefinitionRepository = mock[APIDefinitionRepository]
    val underTest = new APIDefinitionService(mockApiDefinitionRepository)

    when(mockApiDefinitionRepository.save(any())).thenAnswer(returnSame[APIDefinition])
  }

  "createOrUpdate" should {

    "create the API Definition in the repository" in new Setup {

      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(successful(None))

      val result = await(underTest.createOrUpdate(apiDefinition))

      result shouldBe apiDefinition
      verify(mockApiDefinitionRepository).save(apiDefinition)
    }

    "update the API Definition in the repository" in new Setup {
      val updatedApiDefinition = apiDefinition.copy(name = "updatedName")

      given(mockApiDefinitionRepository.fetchByContext(updatedApiDefinition.context)).willReturn(successful(Some(apiDefinition)))

      val result = await(underTest.createOrUpdate(updatedApiDefinition))

      result shouldBe updatedApiDefinition
      verify(mockApiDefinitionRepository).save(updatedApiDefinition)
    }

    "fail to create the definition in the repository if context for another service name already exists" in new Setup {
      val otherApiDefinition = apiDefinition.copy(serviceName = "anotherService")

      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(successful(Some(otherApiDefinition)))

      intercept[ContextAlreadyDefinedForAnotherServiceException] {
        await(underTest.createOrUpdate(apiDefinition))
      }
    }

    "fail when the repository fails" in new Setup {

      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(failed(new RuntimeException("Error message")))

      intercept[RuntimeException] {
        await(underTest.createOrUpdate(apiDefinition))
      }
    }
  }

  "fetchByContext" should {
    "return the api when it exists" in new Setup {
      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(successful(Some(apiDefinition)))

      val result = await(underTest.fetchByContext(apiDefinition.context))

      result shouldBe Some(apiDefinition)
    }

    "return None when the API does not exist" in new Setup {
      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(successful(None))

      val result = await(underTest.fetchByContext(apiDefinition.context))

      result shouldBe None
    }

    "fail when the repository fails" in new Setup {
      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(failed(new RuntimeException("Error message")))

      intercept[RuntimeException] {
        await(underTest.fetchByContext(apiDefinition.context))
      }
    }

  }

  "findAll" should {
    "return the all the apis" in new Setup {
      given(mockApiDefinitionRepository.findAll()).willReturn(successful(Seq(apiDefinition)))

      val result = await(underTest.findAll())

      result shouldBe Seq(apiDefinition)
    }

  }
}