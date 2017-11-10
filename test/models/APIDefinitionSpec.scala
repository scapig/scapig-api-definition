package models

import org.scalatest.{Matchers, WordSpec}

class APIDefinitionSpec extends WordSpec with Matchers {

  val apiEndpoint = Endpoint("/today", "Get Today's Date", HttpMethod.GET, AuthType.NONE)
  val apiVersion = APIVersion("1.0", "http://localhost:8080", APIStatus.PROTOTYPED, Seq(apiEndpoint))
  val apiDefinition = APIDefinition("Calendar API", "My Calendar API", "calendar", Seq(apiVersion))

  "APIDefinition" should {

    "fail validation if a version number is referenced more than once" in {
      lazy val underTest: APIDefinition = apiDefinition.copy(versions = apiDefinition.versions ++ apiDefinition.versions)
      assertValidationFailure(underTest, "version numbers must be unique")
    }

    "fail validation if an empty name is provided" in {
      lazy val underTest: APIDefinition =  apiDefinition.copy(name = "")
      assertValidationFailure(underTest, "name is required")
    }

    "fail validation if an empty context is provided" in {
      lazy val underTest: APIDefinition =  apiDefinition.copy(context = "")
      assertValidationFailure(underTest, "context is required")
    }

    "fail validation if an empty description is provided" in {
      lazy val underTest: APIDefinition =  apiDefinition.copy(description = "")
      assertValidationFailure(underTest, "description is required")
    }

    "fail validation when no APIVersion is provided" in {
      lazy val underTest: APIDefinition =  apiDefinition.copy(versions = Seq())
      assertValidationFailure(underTest, "at least one version is required")
    }

    "fail validation when no Endpoint is provided" in {
      lazy val underTest: APIDefinition =  apiDefinition.copy(versions = Seq(apiVersion.copy(endpoints = Seq.empty)))
      assertValidationFailure(underTest, "at least one endpoint is required")
    }

    "fail validation when no parameter name is provided" in {
      lazy val endpoint: Endpoint = apiEndpoint.copy(queryParameters = Seq(Parameter("")))
      lazy val underTest: APIDefinition =  apiDefinition.copy(versions = Seq(apiVersion.copy(endpoints = Seq(endpoint))))
      assertValidationFailure(underTest, "parameter name is required")
    }

    "fail validation when a scope is not provided but auth type is 'user'" in {
      lazy val endpoint: Endpoint = apiEndpoint.copy(scope = None, authType = AuthType.USER)
      lazy val underTest: APIDefinition =  apiDefinition.copy(versions = Seq(apiVersion.copy(endpoints = Seq(endpoint))))
      assertValidationFailure(underTest, "scope is required if authType is USER")
    }

    "fail validation when a scope is provided but auth type is 'application'" in {
      lazy val endpoint: Endpoint = apiEndpoint.copy(scope = Some("scope"), authType = AuthType.APPLICATION)
      lazy val underTest: APIDefinition =  apiDefinition.copy(versions = Seq(apiVersion.copy(endpoints = Seq(endpoint))))
      assertValidationFailure(underTest, "scope is not required if authType is APPLICATION")
    }
  }

  "APIVersion" should {
    "fail validation if an empty serviceBaseUrl is provided" in {
      lazy val underTest: APIVersion =  apiVersion.copy(serviceBaseUrl = "")
      assertValidationFailure(underTest, "serviceBaseUrl is required")
    }
  }

  private def assertValidationFailure(instance: => AnyRef, failureMessage: String): Unit = {
    try {
      instance
      fail("IllegalArgumentException was expected but not thrown")
    } catch {
      case e: IllegalArgumentException =>
        e.getMessage shouldBe s"requirement failed: $failureMessage"
    }
  }
}
