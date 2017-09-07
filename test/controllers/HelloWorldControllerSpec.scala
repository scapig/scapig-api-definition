package controllers

import org.scalatest.{Matchers, WordSpec}

class HelloWorldControllerSpec extends WordSpec with Matchers {

  "hello" should {
    "work" in {
      true shouldBe true
    }
  }
}
