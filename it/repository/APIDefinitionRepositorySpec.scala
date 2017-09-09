package repository

import javax.inject.Singleton

import models._
import play.api.inject.guice.GuiceApplicationBuilder
import utils.UnitSpec
import play.api.Application

@Singleton
class APIDefinitionRepositorySpec extends UnitSpec {

  val apiEndpoint = Endpoint("/today", "Get Today's Date", HttpMethod.GET, AuthType.NONE)
  val apiVersion = APIVersion("1.0", APIStatus.PROTOTYPED, Seq(apiEndpoint))
  val apiDefinition = APIDefinition("calendar", "/", "Calendar API", "My Calendar API", "calendar", Seq(apiVersion))

  lazy val fakeApplication: Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> "mongodb://localhost:27017/third-party-application-test")
    .build()

  trait Setup {
    val underTest = fakeApplication.injector.instanceOf[APIDefinitionRepository]
  }

  "save" should {
    "insert a new api-definition" in new Setup {
      await(underTest.save(apiDefinition))

      await(underTest.fetchByContext(apiDefinition.context)) shouldBe Some(apiDefinition)
    }

    "update an existing api-definition" in new Setup {
      val updatedApiDefinition = apiDefinition.copy(name = "updatedName")
      await(underTest.save(apiDefinition))

      await(underTest.save(updatedApiDefinition))

      await(underTest.fetchByContext(apiDefinition.context)) shouldBe Some(updatedApiDefinition)
    }

  }
}
