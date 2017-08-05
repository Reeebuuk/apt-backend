package hr.com.blanka.apartments

import java.io.File
import java.time.{ LocalDate, ZoneOffset }

import akka.actor.{ ActorRef, ActorSystem }
import akka.event.{ LoggingAdapter, NoLogging }
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
import akka.persistence.cassandra.testkit.CassandraLauncher
import hr.com.blanka.apartments.Main._
import hr.com.blanka.apartments.command.CommandActor
import hr.com.blanka.apartments.http.model._
import hr.com.blanka.apartments.query.QueryActor
import org.scalatest.concurrent.Eventually
import org.scalatest.time.{ Seconds, Span }
import org.scalatest.{ FlatSpec, Matchers }
import play.api.libs.json.{ Json, OWrites, Reads }

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.language.implicitConversions

class BookingTest extends FlatSpec with Matchers with ScalatestRouteTest with Eventually {

  CassandraLauncher.start(
    new File("cassandra"),
    CassandraLauncher.DefaultTestConfigResource,
    clean = true,
    port = 9042
  )

  implicit val enquiryRequestWrites: OWrites[EnquiryRequest] =
    Json.writes[EnquiryRequest]
  implicit val enquiryReceivedRequestWrites: OWrites[EnquiryReceivedRequest] =
    Json.writes[EnquiryReceivedRequest]
  implicit val depositPaidRequestWrites: OWrites[DepositPaidRequest] =
    Json.writes[DepositPaidRequest]

  implicit val availableApartmentsReads: Reads[AvailableApartmentsResponse] =
    Json.reads[AvailableApartmentsResponse]

  implicit val bookedDayResponseReads: Reads[BookedDayResponse] =
    Json.reads[BookedDayResponse]

  implicit val bookedDaysResponseReads: Reads[BookedDaysResponse] =
    Json.reads[BookedDaysResponse]

  protected val log: LoggingAdapter = NoLogging

//  override def testConfig: Config =
//    IntegrationConf.config(IntegrationConf.freePort, classOf[BookingTest].getSimpleName)

  implicit val ec: ExecutionContextExecutor = system.dispatcher

  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(new DurationInt(10).second)

  val command: ActorRef = system.actorOf(CommandActor(), "commandActor")
  val query: ActorRef   = system.actorOf(QueryActor(materializer), "queryActor")

  val midYearDate: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(5)

  implicit val config = PatienceConfig(Span(10, Seconds), Span(2, Seconds))

  "Booking service" should "save booking and update availability" in {
    val userId = "user"
    val unitId = 1

    val firstFrom  = midYearDate
    val firstTo    = firstFrom.plusDays(5)
    val enquiry    = EnquiryRequest(unitId, firstFrom, firstTo, "", "", "", "", "", "", "", "", "", "")
    val firstPrice = EnquiryReceivedRequest(userId, enquiry)
    val firstRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(firstPrice).toString())

    Post("/booking", firstRequestEntity) ~> commandBookingRoute(command) ~> check {
      status should be(OK)
    }

    val depositPaid = DepositPaidRequest(userId, 1, BigDecimal(15), "EUR")
    val depositPaidEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(depositPaid).toString())

    Post("/booking/depositPaid", depositPaidEntity) ~> commandBookingRoute(command) ~> check {
      status should be(OK)
    }

    eventually {
      Get(
        s"/booking/available?from=${firstFrom.atStartOfDay().toEpochSecond(ZoneOffset.UTC)}&to=${firstTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC)}"
      ) ~> queryBookingRoute(query) ~> check {
        status should be(OK)
        Json.parse(response.entity.toString).as[AvailableApartmentsResponse] should be(
          AvailableApartmentsResponse(Set(2, 3))
        )
      }
    }

    val bookedDays = BookedDaysResponse(
      List(
        BookedDayResponse(firstFrom, firstDay = true, lastDay = false),
        BookedDayResponse(firstFrom.plusDays(1), firstDay = false, lastDay = false),
        BookedDayResponse(firstFrom.plusDays(2), firstDay = false, lastDay = false),
        BookedDayResponse(firstFrom.plusDays(3), firstDay = false, lastDay = false),
        BookedDayResponse(firstFrom.plusDays(4), firstDay = false, lastDay = false),
        BookedDayResponse(firstFrom.plusDays(5), firstDay = false, lastDay = true)
      )
    )

    eventually {
      Get(s"/booking/bookedDates?unitId=$unitId") ~> queryBookingRoute(query) ~> check {
        status should be(OK)
        Json.parse(response.entity.toString).as[BookedDaysResponse] should be(bookedDays)
      }
    }
  }
}
