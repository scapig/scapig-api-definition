package services

import models.{APIVersionRequest, _}
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.mockito.MockitoSugar
import repository.APIDefinitionRepository
import utils.UnitSpec

import scala.concurrent.Future.{failed, successful}

class APIDefinitionServiceSpec extends UnitSpec with MockitoSugar {

  val apiEndpoint = Endpoint("/today", "Get Today's Date", HttpMethod.GET, AuthType.NONE)
  val apiVersion = APIVersion("1.0", "http://localhost:8080", APIStatus.PROTOTYPED, Seq(apiEndpoint))
  val apiDefinition = APIDefinition("Calendar API", "My Calendar API", "calendar", Seq(apiVersion))

  trait Setup {
    val mockApiDefinitionRepository = mock[APIDefinitionRepository]
    val underTest = new APIDefinitionService(mockApiDefinitionRepository)

    when(mockApiDefinitionRepository.save(any())).thenAnswer(returnSame[APIDefinition])
  }

  "createOrUpdate" should {
    val apiVersionRequest = APIVersionRequest("calendar", "Calendar API", "My Calendar API", "1.0",
      "http://localhost:8080", APIStatus.PROTOTYPED, Seq(apiEndpoint))

    "create the API Definition in the repository when it does not exist" in new Setup {

      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(successful(None))

      val result = await(underTest.createOrUpdate(apiVersionRequest))

      result shouldBe apiDefinition
      verify(mockApiDefinitionRepository).save(apiDefinition)
    }

    "update the version of the API Definition when the version exists" in new Setup {
      val requestWithUpdatedVersion = apiVersionRequest.copy(status = APIStatus.PUBLISHED)
      val expectedApi = apiDefinition.copy(versions = Seq(apiVersion.copy(status = APIStatus.PUBLISHED)))

      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(successful(Some(apiDefinition)))

      val result = await(underTest.createOrUpdate(requestWithUpdatedVersion))

      result shouldBe expectedApi
      verify(mockApiDefinitionRepository).save(expectedApi)
    }

    "update the name and description of the API" in new Setup {
      val requestWithNewAPINameAndDescription = apiVersionRequest.copy(apiName = "updatedName", apiDescription = "updatedDescription")
      val expectedApi = apiDefinition.copy(name = "updatedName", description = "updatedDescription")

      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(successful(Some(apiDefinition)))

      val result = await(underTest.createOrUpdate(requestWithNewAPINameAndDescription))

      result shouldBe expectedApi
      verify(mockApiDefinitionRepository).save(expectedApi)
    }

    "add the version of the API Definition when the API exists and does not contain the version" in new Setup {
      val requestWithNewVersion = apiVersionRequest.copy(version = "2.0")
      val expectedVersion2 = apiVersion.copy(version = "2.0")
      val expectedApi = apiDefinition.copy(versions = apiDefinition.versions :+ expectedVersion2)

      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(successful(Some(apiDefinition)))

      val result = await(underTest.createOrUpdate(requestWithNewVersion))

      result shouldBe expectedApi
      verify(mockApiDefinitionRepository).save(expectedApi)
    }

    "fail when the repository fails" in new Setup {

      given(mockApiDefinitionRepository.fetchByContext(apiDefinition.context)).willReturn(failed(new RuntimeException("Error message")))

      intercept[RuntimeException] {
        await(underTest.createOrUpdate(apiVersionRequest))
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