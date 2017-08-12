package hr.com.blanka.apartments

import java.time.ZoneOffset

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.config.Config
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.Generators._
import hr.com.blanka.apartments.Main._
import hr.com.blanka.apartments.http.model._
import hr.com.blanka.apartments.http.routes.command.BookingId
import play.api.libs.json.Json

import scala.language.implicitConversions

class BookingTest extends BaseIntegrationTest {

  import PlayJsonSupport._

  override def testConfig: Config =
    IntegrationConf.config(classOf[BookingTest].getSimpleName, cassandraPort)

  "Booking service should save booking and update availability" in {

    val enquiryRequest = generateEnquiryReceivedRequest()
    val enquiryRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(enquiryRequest).toString())

    Post("/booking", enquiryRequestEntity) ~> commandBookingRoute(command) ~> check {
      status should be(OK)
      val bookingId = Unmarshal(response.entity.httpEntity)
        .to[BookingId]
        .value
        .get
        .get
        .bookingId

      val depositPaid = generateDepositPaidRequest(bookingId)
      val depositPaidEntity =
        HttpEntity(MediaTypes.`application/json`, Json.toJson(depositPaid).toString())

      Post("/booking/depositPaid", depositPaidEntity) ~> commandBookingRoute(command) ~> check {
        status should be(OK)
      }

      eventually {
        Get(
          s"/booking/available?" +
          s"from=${enquiryRequest.enquiry.dateFrom.atStartOfDay().toEpochSecond(ZoneOffset.UTC)}&" +
          s"to=${enquiryRequest.enquiry.dateTo.atStartOfDay().toEpochSecond(ZoneOffset.UTC)}"
        ) ~> queryBookingRoute(query) ~> check {
          status should be(OK)

          Unmarshal(response.entity.httpEntity)
            .to[AvailableApartmentsResponse]
            .value
            .get
            .get shouldBe AvailableApartmentsResponse(Set(2, 3))
        }
      }

      val bookedDays =
        generateBookedDaysResponse(enquiryRequest.enquiry.dateFrom, enquiryRequest.enquiry.dateTo)

      eventually {
        Get(s"/booking/bookedDates?unitId=${enquiryRequest.enquiry.unitId}") ~> queryBookingRoute(
          query
        ) ~> check {
          status should be(OK)

          Unmarshal(response.entity.httpEntity)
            .to[BookedDaysResponse]
            .value
            .get
            .get shouldBe be(bookedDays)
        }
      }
    }

  }
}
