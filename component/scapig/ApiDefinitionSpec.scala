package scapig

import models._
import models.JsonFormatters._
import play.api.http.Status.NO_CONTENT
import play.api.http.Status.OK
import play.api.libs.json.Json
import play.mvc.Http.HeaderNames._

import scalaj.http.Http

class ApiDefinitionSpec extends BaseFeatureSpec {

  val apiEndpoint = Endpoint("/today", "Get Today's Date", HttpMethod.GET, AuthType.NONE)
  val apiVersion = APIVersion("1.0", "http://localhost:8080", APIStatus.PROTOTYPED, Seq(apiEndpoint))
  val apiDefinition = APIDefinition("Calendar API", "My Calendar API", "calendar", Seq(apiVersion))

  val apiVersionRequest = APIVersionRequest("calendar", "Calendar API", "My Calendar API", "1.0",
    "http://localhost:8080", APIStatus.PROTOTYPED, Seq(apiEndpoint))

  feature("create and fetch api definition") {

    scenario("happy path") {

      When("An api-definition create request is received")
      val createdResponse = Http(s"$serviceUrl/api-definition")
        .headers(Seq(CONTENT_TYPE -> "application/json"))
        .postData(Json.toJson(apiVersionRequest).toString()).asString

      Then("I receive a 200 (OK) with the API")
      createdResponse.code shouldBe OK
      Json.parse(createdResponse.body) shouldBe Json.toJson(apiDefinition)

      And("The api-definition can be retrieved by context")
      val fetchResponse = Http(s"$serviceUrl/api-definition?context=${apiDefinition.context}").asString
      fetchResponse.code shouldBe OK
      Json.parse(fetchResponse.body) shouldBe Json.toJson(apiDefinition)

      And("The api-definition is retrieved when fetching all")
      val fetchAllResponse = Http(s"$serviceUrl/apis").asString
      fetchAllResponse.code shouldBe OK
      Json.parse(fetchAllResponse.body) shouldBe Json.toJson(Seq(apiDefinition))


    }
  }
}