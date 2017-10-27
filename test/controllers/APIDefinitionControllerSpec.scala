package controllers

import models.{ContextAlreadyDefinedForAnotherService, _}
import models.JsonFormatters._
import org.mockito.BDDMockito.given
import org.mockito.Matchers.{any, refEq}
import org.mockito.Mockito.{verify, verifyZeroInteractions, when}
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, Helpers}
import services.APIDefinitionService
import utils.UnitSpec

import scala.concurrent.Future
import scala.concurrent.Future.{failed, successful}

class APIDefinitionControllerSpec extends UnitSpec with MockitoSugar {

  val apiEndpoint = Endpoint("/today", "Get Today's Date", HttpMethod.GET, AuthType.NONE)
  val apiVersion = APIVersion("1.0", APIStatus.PROTOTYPED, Seq(apiEndpoint))
  val apiDefinition = APIDefinition("calendar", "/", "Calendar API", "My Calendar API", "calendar", Seq(apiVersion))

  trait Setup {
    val mockApiDefinitionService: APIDefinitionService = mock[APIDefinitionService]
    val underTest = new APIDefinitionController(Helpers.stubControllerComponents(), mockApiDefinitionService)

    val request = FakeRequest()

    given(mockApiDefinitionService.createOrUpdate(apiDefinition)).willReturn(successful(apiDefinition))
  }

  "createOrUpdate" should {

    "succeed with a 204 (NoContent) when payload is valid and service responds successfully" in new Setup {

      val result: Result = await(underTest.createOrUpdate()(request.withBody(Json.toJson(apiDefinition))))

      status(result) shouldBe Status.NO_CONTENT
      verify(mockApiDefinitionService).createOrUpdate(apiDefinition)
    }

    "fail with a 400 (Bad Request) when the json payload is invalid for the request" in new Setup {

      val body = """{ "invalid": "json" }"""

      val result: Result = await(underTest.createOrUpdate()(request.withBody(Json.parse(body))))

      status(result) shouldBe Status.BAD_REQUEST
      jsonBodyOf(result) shouldBe Json.parse("""{"code":"INVALID_REQUEST","message":"serviceBaseUrl is required"}""")
      verifyZeroInteractions(mockApiDefinitionService)
    }

    "fail with a 409 (conflict) when the context was already defined for another service " in new Setup {

      given(mockApiDefinitionService.createOrUpdate(refEq(apiDefinition)))
        .willReturn(failed(ContextAlreadyDefinedForAnotherServiceException(apiDefinition.context, apiDefinition.serviceName)))

      val result = await(underTest.createOrUpdate()(request.withBody(Json.toJson(apiDefinition))))

      status(result) shouldBe Status.CONFLICT

      (jsonBodyOf(result) \ "message").as[String] shouldBe "Context is already defined for another service. It must be unique per service."
      (jsonBodyOf(result) \ "code").as[String] shouldBe "CONTEXT_ALREADY_DEFINED"
    }

  }

  "fetchByContext" should {

    "succeed with a 200 (Ok) with the api-definition when the API exists" in new Setup {

      given(mockApiDefinitionService.fetchByContext(apiDefinition.context))
        .willReturn(successful(Some(apiDefinition)))

      val result: Result = await(underTest.fetchByContext(apiDefinition.context)(request))

      status(result) shouldBe Status.OK
      jsonBodyOf(result) shouldBe Json.toJson(apiDefinition)
    }

    "fail with a 404 (Not Found) when the api-definition does not exist" in new Setup {
      given(mockApiDefinitionService.fetchByContext(apiDefinition.context)).willReturn(successful(None))

      val result: Result = await(underTest.fetchByContext(apiDefinition.context)(request))

      status(result) shouldBe Status.NOT_FOUND
      jsonBodyOf(result) shouldBe Json.parse(s"""{"code": "NOT_FOUND", "message": "no api found for context ${apiDefinition.context}"}""")
    }
  }

  "findAll" should {

    "succeed with a 200 (Ok) with all the api-definitions" in new Setup {

      given(mockApiDefinitionService.findAll())
        .willReturn(successful(Seq(apiDefinition)))

      val result: Result = await(underTest.findAll()(request))

      status(result) shouldBe Status.OK
      jsonBodyOf(result) shouldBe Json.toJson(Seq(apiDefinition))
    }

  }
}