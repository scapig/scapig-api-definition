package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, BaseController, ControllerComponents}

@Singleton
class HelloWorldController  @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def count = Action { Ok("Done") }
}
