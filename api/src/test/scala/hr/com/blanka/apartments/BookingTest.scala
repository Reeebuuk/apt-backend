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
import hr.com.blanka.apartments.query.booking.{AvailableApartments, BookedDay, BookedDays}
import org.joda.time.{DateTime, DateTimeZone, LocalDate}
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

  val midYearDate = new LocalDate().withMonthOfYear(11).withDayOfMonth(5)

  implicit val config = PatienceConfig(Span(10, Seconds), Span(2, Seconds))

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
      Get(s"/booking/available?from=${firstFrom.toDateTimeAtStartOfDay.getMillis}&to=${firstTo.toDateTimeAtStartOfDay.getMillis}") ~> bookingRoute(command, query) ~> check {
        status should be(OK)
        responseAs[AvailableApartments] should be(AvailableApartments(Set(2, 3)))
      }
    }

    val bookedDays = BookedDays(List(
      BookedDay(firstFrom, firstDay = true, lastDay = false),
      BookedDay(firstFrom.plusDays(1), firstDay = false, lastDay = false),
      BookedDay(firstFrom.plusDays(2), firstDay = false, lastDay = false),
      BookedDay(firstFrom.plusDays(3), firstDay = false, lastDay = false),
      BookedDay(firstFrom.plusDays(4), firstDay = false, lastDay = false),
      BookedDay(firstFrom.plusDays(5), firstDay = false, lastDay = true)
    ))

    eventually {
      Get(s"/booking/bookedDates?unitId=$unitId") ~> bookingRoute(command, query) ~> check {
        status should be(OK)
        responseAs[BookedDays] should be(bookedDays)
      }
    }
  }
}

