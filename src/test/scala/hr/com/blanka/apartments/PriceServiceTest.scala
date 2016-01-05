package hr.com.blanka.apartments

import akka.actor.{ActorSystem, Props}
import akka.event.{LoggingAdapter, NoLogging}
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import hr.com.blanka.apartments.Main._
import hr.com.blanka.apartments.http.routes.{CalculatePriceForRangeDto, ErrorResponse, PriceForRangeResponse}
import hr.com.blanka.apartments.price.PriceRangeActor
import hr.com.blanka.apartments.utils.{DateUtils, PricingConfig}
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.DefaultFormats
import org.scalatest.{Matchers, WordSpec}
import spray.json._

import scala.concurrent.duration._

class PriceServiceTest extends IntegrationTestMongoDbSupport with Matchers with ScalatestRouteTest with DateUtils {

  protected val log: LoggingAdapter = NoLogging

  implicit val ec = system.dispatcher

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(new DurationInt(10).second)

  val processor = system.actorOf(Props(classOf[PriceRangeActor], PricingConfig(pricingConfig)), "processorActor")
  val view = system.actorOf(Props(classOf[PriceRangeActor], PricingConfig(pricingConfig)), "viewActor")

  implicit val format = DefaultFormats.withBigDecimal
  val midYearDate = new DateTime().toDateTime(DateTimeZone.UTC).withMonthOfYear(6).withDayOfMonth(5).withTime(12, 0, 0, 0)

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  "Price service" should {
    "retrieve price for single day" in {
      val today = midYearDate.getMillis
      val tomorrow = afterDay(today)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(BigDecimal(35)))
      }
    }


    "return correct price if the duration is 7 day in same price range" in {
      val today = midYearDate.getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(BigDecimal(245)))
      }
    }

    "return correct price if the duration is 7 day in different price ranges" in {
      val today = midYearDate.withMonthOfYear(7).withDayOfMonth(19).getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(BigDecimal(340)))
      }
    }

    "return correct price if the duration is 7 day in different years" in {
      val today = midYearDate.withMonthOfYear(12).withDayOfMonth(30).getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[ErrorResponse] should be(ErrorResponse("UnknownError"))
      }
    }

    "return correct prices for sequential requests" in {
      val today = midYearDate.getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(BigDecimal(245)))
      }

      val today1 = midYearDate.withMonthOfYear(7).withDayOfMonth(19).getMillis
      val tomorrow1 = new DateTime(today1).plusDays(7).getMillis
      val requestEntity1 = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today1, tomorrow1).toJson.toString())

      Post("/v1/price/calculate", requestEntity1) ~> routes(processor, view) ~> check {
        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(BigDecimal(340)))
      }
    }
  }
}

