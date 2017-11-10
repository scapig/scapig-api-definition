package controllers

import models.JsonFormatters._
import models._
import org.mockito.BDDMockito.given
import org.mockito.Mockito.{verify, verifyZeroInteractions}
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, Helpers}
import services.APIDefinitionService
import utils.UnitSpec

import scala.concurrent.Future.successful

class APIDefinitionControllerSpec extends UnitSpec with MockitoSugar {

  val apiEndpoint = Endpoint("/today", "Get Today's Date", HttpMethod.GET, AuthType.NONE)
  val apiVersion = APIVersion("1.0", "http://localhost:8080", APIStatus.PROTOTYPED, Seq(apiEndpoint))
  val apiDefinition = APIDefinition("Calendar API", "My Calendar API", "calendar", Seq(apiVersion))

  val apiVersionRequest = APIVersionRequest("calendar", "Calendar API", "My Calendar API", "1.0",
    "http://localhost:8080", APIStatus.PROTOTYPED, Seq(apiEndpoint))

  trait Setup {
    val mockApiDefinitionService: APIDefinitionService = mock[APIDefinitionService]
    val underTest = new APIDefinitionController(Helpers.stubControllerComponents(), mockApiDefinitionService)

    val request = FakeRequest()

    given(mockApiDefinitionService.createOrUpdate(apiVersionRequest)).willReturn(successful(apiDefinition))
  }

  "createOrUpdate" should {

    "succeed with a 200 (Ok) with the API Definition when payload is valid and service responds successfully" in new Setup {

      val result: Result = await(underTest.createOrUpdate()(request.withBody(Json.toJson(apiVersionRequest))))

      status(result) shouldBe Status.OK
      jsonBodyOf(result) shouldBe Json.toJson(apiDefinition)
      verify(mockApiDefinitionService).createOrUpdate(apiVersionRequest)
    }

    "fail with a 400 (Bad Request) when the json payload is invalid for the request" in new Setup {

      val body = """{ "invalid": "json" }"""

      val result: Result = await(underTest.createOrUpdate()(request.withBody(Json.parse(body))))

      status(result) shouldBe Status.BAD_REQUEST
      jsonBodyOf(result) shouldBe Json.parse("""{"code":"INVALID_REQUEST","message":"endpoints is required"}""")
      verifyZeroInteractions(mockApiDefinitionService)
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