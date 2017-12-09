package repository

import javax.inject.Singleton

import models._
import org.scalatest.BeforeAndAfterEach
import play.api.inject.guice.GuiceApplicationBuilder
import utils.UnitSpec
import play.api.Application
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class APIDefinitionRepositorySpec extends UnitSpec with BeforeAndAfterEach {

  val apiEndpoint = Endpoint("/today", "Get Today's Date", HttpMethod.GET, AuthType.NONE)
  val apiVersion = APIVersion("1.0", "http://localhost:8080", APIStatus.PROTOTYPED, Seq(apiEndpoint))
  val apiDefinition = APIDefinition("Calendar API", "My Calendar API", "calendar", Seq(apiVersion))

  lazy val fakeApplication: Application = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> "mongodb://localhost:27017/scapig-api-definition-test")
    .build()

  lazy val underTest = fakeApplication.injector.instanceOf[APIDefinitionRepository]

  override def afterEach {
    await(underTest.repository).drop(failIfNotFound = false)
  }

  "save" should {
    "insert a new api-definition" in {
      await(underTest.save(apiDefinition))

      await(underTest.fetchByContext(apiDefinition.context)) shouldBe Some(apiDefinition)
    }

    "update an existing api-definition" in {
      val updatedApiDefinition = apiDefinition.copy(name = "updatedName")
      await(underTest.save(apiDefinition))

      await(underTest.save(updatedApiDefinition))

      await(underTest.fetchByContext(apiDefinition.context)) shouldBe Some(updatedApiDefinition)
    }

  }

  "findAll" should {
    "return all api-definition" in {
      val otherApiDefinition = apiDefinition.copy(context = "another")

      await(underTest.save(apiDefinition))
      await(underTest.save(otherApiDefinition))

      await(underTest.findAll()) should contain allOf (apiDefinition, otherApiDefinition)
    }
  }
}