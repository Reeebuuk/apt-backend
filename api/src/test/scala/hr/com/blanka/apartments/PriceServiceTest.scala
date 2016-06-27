package hr.com.blanka.apartments

import akka.actor.{ActorSystem, Props}
import akka.event.{LoggingAdapter, NoLogging}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.stream.ActorMaterializer
import hr.com.blanka.apartments.Main._
import hr.com.blanka.apartments.command.CommandActor
import hr.com.blanka.apartments.command.price.SavePriceRange
import hr.com.blanka.apartments.http.routes.{ErrorResponse, PriceForRangeResponse}
import hr.com.blanka.apartments.query.QueryActor
import hr.com.blanka.apartments.query.price.LookupPriceForRange
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.DefaultFormats
import org.scalatest.concurrent.Eventually
import org.scalatest.{Matchers, WordSpecLike}
import spray.json._

import scala.concurrent.duration._
import scala.language.implicitConversions

class PriceServiceTest extends WordSpecLike with Matchers with ScalatestRouteTest with Eventually {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  protected val log: LoggingAdapter = NoLogging

  implicit val ec = system.dispatcher

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(new DurationInt(10).second)
//  override implicit val materializer: ActorMaterializer = ActorMaterializer()

  val command = system.actorOf(CommandActor(), "commandActor")
  val query = system.actorOf(QueryActor(materializer), "queryActor")

  implicit val format = DefaultFormats.withBigDecimal
  implicit def toMillis(date: DateTime): Long = date.getMillis
  val midYearDate = new DateTime().toDateTime(DateTimeZone.UTC).withMonthOfYear(11).withDayOfMonth(5).withTime(12, 0, 0, 0)

  "Price service save" should {
    "save valid price range" in {
      val from = midYearDate
      val to = midYearDate.plusDays(1)
      val price = SavePriceRange("user", 1, from, to, 35)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, price.toJson.toString())

      Post("/price", requestEntity) ~> priceRoute(command, query) ~> check {
        status should be (OK)
      }

      val lookupRequest = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, from, to).toJson.toString())

      eventually {
        Post("/price/calculate", lookupRequest) ~> priceRoute(command, query) ~> check {
          responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(35))
          status should be(OK)
        }
      }
    }

    "return error message if from is later than to date" in {
      val from = midYearDate
      val to = midYearDate.plusDays(1)
      val price = SavePriceRange("user", 1, to, from, 35)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, price.toJson.toString())

      Post("/price", requestEntity) ~> priceRoute(command, query) ~> check {
        responseAs[ErrorResponse] should be(ErrorResponse("Invalid date range"))
        status should be (BadRequest)
      }
    }

    "return error message if dates are not valid" in {
      val price = SavePriceRange("user", 1, 0, 0, 35)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, price.toJson.toString())

      Post("/price", requestEntity) ~> priceRoute(command, query) ~> check {
        responseAs[ErrorResponse] should be(ErrorResponse("From date is in the past, To date is in the past"))
        status should be (BadRequest)
      }
    }

    "return error message if unit id is wrong" in {
      val from = midYearDate
      val to = midYearDate.plusDays(1)
      val price = SavePriceRange("user", 15, from, to, 35)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, price.toJson.toString())

      Post("/price", requestEntity) ~> priceRoute(command, query) ~> check {
        responseAs[ErrorResponse] should be(ErrorResponse("Unit id doesn't exist"))
        status should be (BadRequest)
      }
    }
  }

  "Price service fetching" should {
    "retrieve price for single day" in {
      val from = midYearDate
      val to = midYearDate.plusDays(1)
      val price = SavePriceRange("user", 1, from, to, 35)
      val saveEntity = HttpEntity(MediaTypes.`application/json`, price.toJson.toString())

      Post("/price", saveEntity) ~> priceRoute(command, query) ~> check {
        status should be (OK)
      }

      val requestEntity = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, from, to).toJson.toString())

      Post("/price/calculate", requestEntity) ~> priceRoute(command, query) ~> check {
        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(35))
        status should be (OK)
      }
    }

    "return correct price if the duration is 7 day in same price range" in {
      val from = midYearDate
      val to = new DateTime(from).plusDays(7)
      val savePrice = HttpEntity(MediaTypes.`application/json`, SavePriceRange("user", 1, from, to, 35).toJson.toString())

      Post("/price", savePrice) ~> priceRoute(command, query) ~> check {
        status should be (OK)
      }

      val queryPrice = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, from, to).toJson.toString())

      eventually {
        Post("/price/calculate", queryPrice) ~> priceRoute(command, query) ~> check {
          responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(245))
        }
      }
    }

    "return correct price if the duration is 7 day in different price ranges" in {
      val saveFrom = midYearDate.minusDays(1)
      val saveTo = new DateTime(saveFrom).plusDays(3)
      val savePrice = HttpEntity(MediaTypes.`application/json`, SavePriceRange("user", 1, saveFrom, saveTo, 35).toJson.toString())

      Post("/price", savePrice) ~> priceRoute(command, query) ~> check {
        status should be (OK)
      }

      val saveFrom1 = saveTo
      val saveTo1 = new DateTime(saveFrom1).plusDays(7)
      val savePrice1 = HttpEntity(MediaTypes.`application/json`, SavePriceRange("user", 1, saveFrom1, saveTo1, 30).toJson.toString())

      Post("/price", savePrice1) ~> priceRoute(command, query) ~> check {
        status should be (OK)
      }


      val from = midYearDate
      val to = new DateTime(from).plusDays(7)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, from, to).toJson.toString())

      eventually {
        Post("/price/calculate", requestEntity) ~> priceRoute(command, query) ~> check {
          responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(220))
        }
      }
    }

    "return correct price if the duration is 7 day in different years" in {
      val from = midYearDate.withMonthOfYear(12).withDayOfMonth(30)
      val to = new DateTime(from).plusDays(7)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, from, to).toJson.toString())

      Post("/price/calculate", requestEntity) ~> priceRoute(command, query) ~> check {
        responseAs[ErrorResponse] should be(ErrorResponse("UnknownError"))
      }
    }

    "return correct prices for sequential requests" in {
      val from = midYearDate
      val to = new DateTime(from).plusDays(7)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, from, to).toJson.toString())

      Post("/price/calculate", requestEntity) ~> priceRoute(command, query) ~> check {
        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(245))
      }

      val from1 = midYearDate.withMonthOfYear(7).withDayOfMonth(19)
      val to1 = new DateTime(from1).plusDays(7)
      val requestEntity1 = HttpEntity(MediaTypes.`application/json`, LookupPriceForRange("user", 1, from1, to1).toJson.toString())

      Post("/price/calculate", requestEntity1) ~> priceRoute(command, query) ~> check {
        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(340))
      }
    }
  }
}

