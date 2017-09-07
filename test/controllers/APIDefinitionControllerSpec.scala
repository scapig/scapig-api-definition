package controllers

import models._
import models.JsonFormatters._
import org.mockito.{BDDMockito, Mockito}
import org.mockito.BDDMockito.given
import org.mockito.Mockito.verify
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc
import play.api.mvc.Result
import play.api.test.{FakeRequest, Helpers}
import services.APIDefinitionService
import utils.UnitSpec

import scala.concurrent.Future
import scala.concurrent.Future.successful

class APIDefinitionControllerSpec extends UnitSpec with MockitoSugar {

  val apiEndpoint = Endpoint("/today", "Get Today's Date", HttpMethod.GET, AuthType.NONE)
  val apiVersion = APIVersion("1.0", APIStatus.PROTOTYPED, Seq(apiEndpoint))
  val apiDefinition = APIDefinition("calendar", "/", "Calendar API", "My Calendar API", "calendar", Seq(apiVersion))

  trait Setup {
    val mockApiDefinitionService: APIDefinitionService = mock[APIDefinitionService]
    val underTest = new APIDefinitionController(Helpers.stubControllerComponents(), mockApiDefinitionService)

    val request = FakeRequest()

  }

  "createOrUpdate" should {

    "succeed with a 204 (NoContent) when payload is valid and service responds successfully" in new Setup {
      given(mockApiDefinitionService.createOrUpdate(apiDefinition)).willReturn(successful(apiDefinition))

      val result: Result = await(underTest.createOrUpdate()(request.withBody(Json.toJson(apiDefinition))))

      status(result) shouldBe Status.NO_CONTENT
//      verify(mockApiDefinitionService).createOrUpdate(apiDefinition)
    }
  }
}