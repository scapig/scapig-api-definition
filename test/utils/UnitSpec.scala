package utils

import java.nio.charset.Charset

import akka.stream.Materializer
import akka.util.ByteString
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result

import scala.concurrent.duration.{Duration, DurationLong}
import scala.concurrent.{Await, Awaitable, Future}

class UnitSpec extends WordSpec with Matchers {

  def await[T](future: Future[T]): T = Await.result(future, 10.seconds)

  def status(of: Future[Result])(implicit timeout: Duration): Int = status(Await.result(of, timeout))
  def status(of: Result): Int = of.header.status

  def jsonBodyOf(result: Result)(implicit mat: Materializer): JsValue = {
    Json.parse(bodyOf(result))
  }
  def bodyOf(result: Result)(implicit mat: Materializer): String = {
    val bodyBytes: ByteString = await(result.body.consumeData)
    bodyBytes.decodeString(Charset.defaultCharset().name)
  }
}
