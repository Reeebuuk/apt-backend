package hr.com.blanka.apartments

import akka.actor.{ActorSystem, Props}
import akka.event.{LoggingAdapter, NoLogging}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import hr.com.blanka.apartments.Main._
import hr.com.blanka.apartments.http.routes.{ErrorResponse, PriceForRangeResponse}
import hr.com.blanka.apartments.price.protocol.{SavePriceRange, LookupPriceForRange}
import hr.com.blanka.apartments.price.{DailyPriceAggregateActor, CommandPriceRangeActor, QueryPriceRangeActor}
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.DefaultFormats
import org.scalatest.{Matchers, WordSpecLike}
import spray.json._

import scala.concurrent.duration._

class PriceServiceTest extends WordSpecLike with Matchers with ScalatestRouteTest {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  protected val log: LoggingAdapter = NoLogging

  implicit val ec = system.dispatcher

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(new DurationInt(10).second)

  val aggregate = system.actorOf(Props(classOf[DailyPriceAggregateActor]), "aggregatedActor")

  val command = system.actorOf(Props(classOf[CommandPriceRangeActor], aggregate), "commandActor")
  val query = system.actorOf(Props(classOf[QueryPriceRangeActor], aggregate), "queryActor")

  implicit val format = DefaultFormats.withBigDecimal
  implicit def toMillis(date: DateTime): Long = date.getMillis
  val midYearDate = new DateTime().toDateTime(DateTimeZone.UTC).withMonthOfYear(6).withDayOfMonth(5).withTime(12, 0, 0, 0)

  "Price service save" should {
    "save valid price range" in {
      val today = midYearDate
      val tomorrow = midYearDate.plusDays(1)
      val newPrice = SavePriceRange("user", 1, today, tomorrow, 35)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, newPrice.toJson.toString())

      Post("/v1/price", requestEntity) ~> routes(command, query) ~> check {
        status should be (OK)
      }
    }

    "return error message if from is later than to date" in {
      val today = midYearDate
      val tomorrow = midYearDate.plusDays(1)
      val newPrice = SavePriceRange("user", 1, tomorrow, today, 35)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, newPrice.toJson.toString())

      Post("/v1/price", requestEntity) ~> routes(command, query) ~> check {
        responseAs[ErrorResponse] should be(ErrorResponse("Invalid date range"))
        status should be (BadRequest)
      }
    }

    "return error message if dates are not valid" in {
      val newPrice = SavePriceRange("user", 1, 0, 0, 35)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, newPrice.toJson.toString())

      Post("/v1/price", requestEntity) ~> routes(command, query) ~> check {
        responseAs[ErrorResponse] should be(ErrorResponse("From date is in the past, To date is in the past"))
        status should be (BadRequest)
      }
    }

    "return error message if unit id is wrong" in {
      val today = midYearDate
      val tomorrow = midYearDate.plusDays(1)
      val newPrice = SavePriceRange("user", 15, today, tomorrow, 35)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, newPrice.toJson.toString())

      Post("/v1/price", requestEntity) ~> routes(command, query) ~> check {
        responseAs[ErrorResponse] should be(ErrorResponse("Unit id doesn't exist"))
        status should be (BadRequest)
      }
    }
  }

  "Price service fetching" should {
    "retrieve price for single day" in {
      val today = midYearDate
      val tomorrow = midYearDate.plusDays(1)
      val newPrice = SavePriceRange("user", 1, today, tomorrow, 35)
      val saveEntity = HttpEntity(MediaTypes.`application/json`, newPrice.toJson.toString())

      Post("/v1/price", saveEntity) ~> routes(command, query) ~> check {
        status should be (OK)
      }

      val requestEntity = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(command, query) ~> check {
        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(35))
        status should be (OK)
      }
    }

//    "return correct price if the duration is 7 day in same price range" in {
//      val today = midYearDate
//      val tomorrow = new DateTime(today).plusDays(7)
//      val requestEntity = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, today, tomorrow).toJson.toString())
//
//      Post("/v1/price/calculate", requestEntity) ~> routes(command, query) ~> check {
//        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(245))
//      }
//    }
//
//    "return correct price if the duration is 7 day in different price ranges" in {
//      val today = midYearDate.withMonthOfYear(7).withDayOfMonth(19)
//      val tomorrow = new DateTime(today).plusDays(7)
//      val requestEntity = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, today, tomorrow).toJson.toString())
//
//      Post("/v1/price/calculate", requestEntity) ~> routes(command, query) ~> check {
//        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(340))
//      }
//    }
//
//    "return correct price if the duration is 7 day in different years" in {
//      val today = midYearDate.withMonthOfYear(12).withDayOfMonth(30)
//      val tomorrow = new DateTime(today).plusDays(7)
//      val requestEntity = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, today, tomorrow).toJson.toString())
//
//      Post("/v1/price/calculate", requestEntity) ~> routes(command, query) ~> check {
//        responseAs[ErrorResponse] should be(ErrorResponse("UnknownError"))
//      }
//    }
//
//    "return correct prices for sequential requests" in {
//      val today = midYearDate
//      val tomorrow = new DateTime(today).plusDays(7)
//      val requestEntity = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, today, tomorrow).toJson.toString())
//
//      Post("/v1/price/calculate", requestEntity) ~> routes(command, query) ~> check {
//        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(245))
//      }
//
//      val today1 = midYearDate.withMonthOfYear(7).withDayOfMonth(19)
//      val tomorrow1 = new DateTime(today1).plusDays(7)
//      val requestEntity1 = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, today1, tomorrow1).toJson.toString())
//
//      Post("/v1/price/calculate", requestEntity1) ~> routes(command, query) ~> check {
//        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(340))
//      }
//    }
  }
}

