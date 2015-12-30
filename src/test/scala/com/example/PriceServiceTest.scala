package com.example

import akka.actor.{ActorSystem, Props}
import akka.event.{NoLogging, LoggingAdapter}
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import akka.http.scaladsl.testkit.{ScalatestRouteTest, RouteTestTimeout}
import com.example.crudapi.Main._
import com.example.crudapi.http.BaseService
import com.example.crudapi.http.routes.{ErrorDto, CalculatePriceForRangeDto, PriceForRangeDto}
import com.example.crudapi.price.PriceRangeActor
import com.example.crudapi.utils.{DateUtils, MarshallingSupport, PricingConfig}
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.DefaultFormats
import org.scalatest.{WordSpec, Matchers, FlatSpec}
import org.scalatest.concurrent.ScalaFutures
import spray.json._

import scala.concurrent.duration._

class PriceServiceTest extends WordSpec with Matchers with ScalatestRouteTest with DateUtils {

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
        responseAs[PriceForRangeDto] should be(PriceForRangeDto(1, BigDecimal(35)))
      }
    }


    "return correct price if the duration is 7 day in same price range" in {
      val today = midYearDate.getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[PriceForRangeDto] should be(PriceForRangeDto(1, BigDecimal(245)))
      }
    }

    "return correct price if the duration is 7 day in different price ranges" in {
      val today = midYearDate.withDate(2015, 7, 19).getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[PriceForRangeDto] should be(PriceForRangeDto(1, BigDecimal(340)))
      }
    }

    "return correct price if the duration is 7 day in different years" in {
      val today = midYearDate.withDate(2015, 12, 30).getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[ErrorDto] should be(ErrorDto(1, "ERROR"))
      }
    }

/*    "support concurrent requests" in {
      val today = midYearDate.withDate(2015, 12, 30).getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[PriceForRangeDto] should be(PriceForRangeDto(1, BigDecimal(245)))
      }

      val today1 = midYearDate.withDate(2015, 7, 19).getMillis
      val tomorrow1 = new DateTime(today1).plusDays(7).getMillis
      val requestEntity1 = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today1, tomorrow1).toJson.toString())

      Post("/v1/price/calculate", requestEntity1) ~> routes(processor, view) ~> check {
        responseAs[PriceForRangeDto] should be(PriceForRangeDto(1, BigDecimal(340)))
      }
    }*/


    /*    "retrieve customer by id" in {
            Get("/customers/1") ~> customersRoute ~> check {
              responseAs[JsObject] should be(testCustomers.head)
            }
          }

          "update customer by id and retrieve it" in {
            val newCustomerfirstname = "UpdatedCustomerfirstname"
            val requestEntity = HttpEntity(MediaTypes.`application/json`, JsObject("firstname" -> JsString(newCustomerfirstname)).toString())
            Post("/customers/1", requestEntity) ~> customersRoute ~> check {
              responseAs[JsObject] should be(testCustomers.head.copy(firstname = newCustomerfirstname))
              //        whenReady(getCustomerById(1)) { result =>
              //          result.get.firstname should be(newCustomerfirstname)
              //        }
            }
          }*/

  }

}

