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

    val firstPrice = generateSavePriceRangeRequest(price = BigDecimal(1)) //5.11-12.11
    val firstRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(firstPrice).toString())

    Post("/price", firstRequestEntity) ~> commandPriceRoute(command) ~> check {
      status should be(OK)
    }

    val enquiryRequest = generateEnquiryRequest()
    val enquiryRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(enquiryRequest).toString())

    //TODO add injectable mailer instance via guice
    Post("/enquiry", enquiryRequestEntity) ~> commandEnquiryRoute(command) ~> check {
      status should be(OK)
      val enquiryId = Unmarshal(response.entity.httpEntity)
        .to[NewEnquiryResponse]
        .eagerExtract
        .enquiryId

      val depositPaid = generateDepositPaidRequest(enquiryId)
      val depositPaidEntity =
        HttpEntity(MediaTypes.`application/json`, Json.toJson(depositPaid).toString())

      Put(s"/enquiry/$enquiryId/authorize") ~> commandEnquiryRoute(command) ~> check {
        status should be(OK)
      }

      Post("/enquiry/depositPaid", depositPaidEntity) ~> commandEnquiryRoute(command) ~> check {
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

      val expectedBookings = generateAllBookingsResponse(List(enquiryId))

      eventually {
        Get(s"/enquiry/booked?year=${enquiryRequest.dateFrom.getYear}") ~> queryEnquiryRoute(
          query
        ) ~> check {
          status should be(OK)

          //TODO add injectable time provider to fix equality
          val res: List[BookingResponse] = Unmarshal(response.entity.httpEntity)
            .to[AllBookingsResponse]
            .eagerExtract
            .enquiries

          res.map(_.enquiryId) should contain theSameElementsAs expectedBookings.enquiries.map(
            _.enquiryId
          )
          res.map(_.totalPrice) should contain theSameElementsAs expectedBookings.enquiries.map(
            _.totalPrice
          )
        }
      }
    }

  }
}
