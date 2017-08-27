package hr.com.blanka.apartments

import java.time.ZoneOffset

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.config.Config
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.Main._
import hr.com.blanka.apartments.base.{ BaseIntegrationTest, IntegrationConf }
import hr.com.blanka.apartments.http.model._
import play.api.libs.json.Json

import scala.language.implicitConversions

class BookingTest extends BaseIntegrationTest {

  import PlayJsonSupport._
  import hr.com.blanka.apartments.util.RequestResponseGenerators._

  override def testConfig: Config =
    IntegrationConf.config(classOf[BookingTest].getSimpleName)

  "Booking service should save booking and update availability" in {

    val enquiryRequest = generateEnquiryRequest()
    val enquiryRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(enquiryRequest).toString())

    //TODO add injectable mailer instance via guice
    Post("/booking", enquiryRequestEntity) ~> commandBookingRoute(command) ~> check {
      status should be(OK)
      val bookingId = Unmarshal(response.entity.httpEntity)
        .to[NewEnquiryResponse]
        .eagerExtract
        .bookingId

      val depositPaid = generateDepositPaidRequest(bookingId)
      val depositPaidEntity =
        HttpEntity(MediaTypes.`application/json`, Json.toJson(depositPaid).toString())

      Put(s"/booking/$bookingId/authorize") ~> commandBookingRoute(command) ~> check {
        status should be(OK)
      }

      Post("/booking/depositPaid", depositPaidEntity) ~> commandBookingRoute(command) ~> check {
        status should be(OK)
      }

      eventually {
        Get(
          s"/booking/available?" +
          s"from=${enquiryRequest.dateFrom.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli}&" +
          s"to=${enquiryRequest.dateTo.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli}"
        ) ~> queryBookingRoute(query) ~> check {
          status should be(OK)

          Unmarshal(response.entity.httpEntity)
            .to[AvailableUnitsResponse]
            .eagerExtract shouldBe AvailableUnitsResponse(Set(2, 3))
        }
      }

      val expectedBookedDates: BookedDatesResponse =
        generateBookedDaysResponse(enquiryRequest.dateFrom, enquiryRequest.dateTo)

      eventually {
        Get(s"/booking/bookedDates?unitId=${enquiryRequest.unitId}") ~> queryBookingRoute(
          query
        ) ~> check {
          status should be(OK)

          Unmarshal(response.entity.httpEntity)
            .to[BookedDatesResponse]
            .eagerExtract
            .bookedDays should contain theSameElementsAs expectedBookedDates.bookedDays
        }
      }

      val expectedBookings = generateAllBookingsResponse(List(bookingId))

      eventually {
        Get("/booking") ~> queryBookingRoute(
          query
        ) ~> check {
          status should be(OK)

          //TODO add injectable time provider to fix equality
          Unmarshal(response.entity.httpEntity)
            .to[AllBookingsResponse]
            .eagerExtract
            .bookings
            .map(_.bookingId) should contain theSameElementsAs expectedBookings.bookings.map(
            _.bookingId
          )
        }
      }
    }

  }
}
