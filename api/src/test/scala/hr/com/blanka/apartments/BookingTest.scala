package hr.com.blanka.apartments

import akka.actor.ActorSystem
import akka.event.{LoggingAdapter, NoLogging}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.typesafe.config.Config
import hr.com.blanka.apartments.Main._
import hr.com.blanka.apartments.command.CommandActor
import hr.com.blanka.apartments.command.booking.{DepositPaid, Enquiry, EnquiryReceived}
import hr.com.blanka.apartments.http.routes.PriceForRangeResponse
import hr.com.blanka.apartments.query.QueryActor
import hr.com.blanka.apartments.query.booking.{AvailableApartments, BookedDays}
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.DefaultFormats
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{Second, Seconds, Span}
import spray.json._

import scala.concurrent.duration._
import scala.language.implicitConversions

class BookingTest extends IntegrationTestMongoDbSupport with Matchers with ScalatestRouteTest with Eventually {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  protected val log: LoggingAdapter = NoLogging

  override def testConfig: Config = IntegrationConf.config(IntegrationConf.freePort, classOf[BookingTest].getSimpleName)

  implicit val ec = system.dispatcher

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(new DurationInt(10).second)

  val command = system.actorOf(CommandActor(), "commandActor")
  val query = system.actorOf(QueryActor(materializer), "queryActor")

  implicit val format = DefaultFormats.withBigDecimal

  implicit def toMillis(date: DateTime): Long = date.getMillis

  val midYearDate = new DateTime().toDateTime(DateTimeZone.UTC).withMonthOfYear(11).withDayOfMonth(5).withTime(12, 0, 0, 0)

  implicit val config = PatienceConfig(Span(5, Seconds), Span(1, Second))

  "Booking service" should "save booking and update availability" in {
    val userId = "user"
    val unitId = 1

    val firstFrom = midYearDate
    val firstTo = firstFrom.plusDays(5)
    val enquiry = Enquiry(unitId, firstFrom, firstTo, "", "", "", "", "", "", "", "", "", "")
    val firstPrice = EnquiryReceived(userId, enquiry)
    val firstRequestEntity = HttpEntity(MediaTypes.`application/json`, firstPrice.toJson.toString())

    Post("/booking", firstRequestEntity) ~> bookingRoute(command, query) ~> check {
      status should be(OK)
    }

    val depositPaid = DepositPaid(userId, 1, BigDecimal(15), "EUR")
    val depositPaidEntity = HttpEntity(MediaTypes.`application/json`, depositPaid.toJson.toString())

    Post("/booking/depositPaid", depositPaidEntity) ~> bookingRoute(command, query) ~> check {
      status should be(OK)
    }

    eventually {
      Get(s"/booking/available?from=${firstFrom.getMillis}&to=${firstTo.getMillis}") ~> bookingRoute(command, query) ~> check {
        status should be(OK)
        responseAs[AvailableApartments] should be(AvailableApartments(Set()))
      }
    }

    eventually {
      Get(s"/booking/bookedDates?unitId=$unitId") ~> bookingRoute(command, query) ~> check {
        status should be(OK)
        responseAs[BookedDays] should be(BookedDays(List()))
      }
    }
  }
}

