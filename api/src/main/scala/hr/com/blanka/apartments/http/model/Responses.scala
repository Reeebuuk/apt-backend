package hr.com.blanka.apartments.http.model

import java.time.{ LocalDate, ZoneOffset }

import hr.com.blanka.apartments.common.Enquiry
import hr.com.blanka.apartments.query.booking._
import hr.com.blanka.apartments.query.price.PricePerPeriod

case class PriceForRangeResponse(price: BigDecimal)

case class ErrorResponse(msg: String)

case class BookedDatesResponse(bookedDays: List[BookedDateResponse])
object BookedDatesResponse {
  def remap(bd: BookedDays): BookedDatesResponse =
    BookedDatesResponse(bd.bookedDays.map(BookedDateResponse.remap))
}

case class AvailableUnitsResponse(unitIds: Set[Int])
object AvailableUnitsResponse {
  def remap(au: AvailableUnits): AvailableUnitsResponse =
    new AvailableUnitsResponse(au.unitIds.map(_.id))
}

case class BookedDateResponse(day: LocalDate, firstDay: Boolean, lastDay: Boolean)
object BookedDateResponse {
  def remap(bd: BookedDay): BookedDateResponse =
    BookedDateResponse(bd.day, bd.firstDay, bd.lastDay)
}

case class NewEnquiryResponse(enquiryId: Long)

case class PricePerPeriodResponse(from: Long, to: Long, appPrice: Map[Int, Int])
case class PricePerPeriodsResponse(prices: List[PricePerPeriodResponse])
object PricePerPeriodsResponse {
  def remap(ppp: List[PricePerPeriod]): PricePerPeriodsResponse =
    PricePerPeriodsResponse(
      ppp.map(
        period =>
          PricePerPeriodResponse(
            from = LocalDate
              .of(LocalDate.now().getYear, period.from.month, period.from.day)
              .atStartOfDay()
              .toInstant(ZoneOffset.UTC)
              .toEpochMilli,
            to = LocalDate
              .of(LocalDate.now().getYear, period.to.month, period.to.day)
              .atStartOfDay()
              .toInstant(ZoneOffset.UTC)
              .toEpochMilli,
            appPrice = period.appPrice.map(x => (x._1.id, x._2))
        )
      )
    )
}

case class EnquiryResponse(unitId: Int,
                           dateFrom: Long,
                           dateTo: Long,
                           name: String,
                           surname: String,
                           phoneNumber: String,
                           email: String,
                           country: String,
                           animals: String,
                           noOfPeople: String,
                           note: String)

object EnquiryResponse {
  def remap(e: Enquiry): EnquiryResponse =
    EnquiryResponse(
      unitId = e.unitId.id,
      dateFrom = e.dateFrom.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
      dateTo = e.dateTo.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
      name = e.name,
      surname = e.surname,
      phoneNumber = e.phoneNumber,
      email = e.email,
      country = e.country,
      animals = e.animals,
      noOfPeople = e.noOfPeople,
      note = e.note
    )
}

case class AllBookingsResponse(enquiries: List[BookingResponse])
case class BookingResponse(enquiryId: Long,
                           enquiryDttm: Long,
                           approvalDttm: Long,
                           enquiry: EnquiryResponse,
                           totalPrice: BigDecimal,
                           depositAmount: BigDecimal,
                           depositCurrency: String,
                           depositWhen: Long)

object AllBookingsResponse {
  def remap(allBookings: AllBookedEnquiries): AllBookingsResponse =
    AllBookingsResponse(
      allBookings.bookings.map(
        b =>
          BookingResponse(
            enquiryId = b.enquiryId.id,
            enquiryDttm = b.enquiryDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            approvalDttm = b.approvedDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            enquiry = EnquiryResponse.remap(b.enquiry),
            totalPrice = BigDecimal(0),
            depositAmount = b.bookingDeposit.amount,
            depositCurrency = b.bookingDeposit.currency,
            depositWhen = b.bookingDeposit.when.toInstant(ZoneOffset.UTC).toEpochMilli
        )
      )
    )
}

case class AllApprovedEnquiriesResponse(enquiries: List[ApprovedEnquiryResponse])
case class ApprovedEnquiryResponse(enquiryId: Long,
                                   enquiryDttm: Long,
                                   approvalDttm: Long,
                                   enquiry: EnquiryResponse,
                                   totalPrice: BigDecimal)

object AllApprovedEnquiriesResponse {
  def remap(allApprovedEnquiries: AllApprovedEnquiries): AllApprovedEnquiriesResponse =
    AllApprovedEnquiriesResponse(
      allApprovedEnquiries.enquiries.map(
        b =>
          ApprovedEnquiryResponse(
            enquiryId = b.enquiryId.id,
            enquiryDttm = b.enquiryDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            approvalDttm = b.approvedDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            enquiry = EnquiryResponse.remap(b.enquiry),
            totalPrice = BigDecimal(0)
        )
      )
    )
}

case class AllUnapprovedEnquiriesResponse(enquiries: List[UnapprovedEnquiryResponse])
case class UnapprovedEnquiryResponse(enquiryId: Long,
                                     enquiryDttm: Long,
                                     enquiry: EnquiryResponse,
                                     totalPrice: BigDecimal)

object AllUnapprovedEnquiriesResponse {
  def remap(allUnapprovedEnquiries: AllUnapprovedEnquiries): AllUnapprovedEnquiriesResponse =
    AllUnapprovedEnquiriesResponse(
      allUnapprovedEnquiries.enquiries.map(
        b =>
          UnapprovedEnquiryResponse(
            enquiryId = b.enquiryId.id,
            enquiryDttm = b.enquiryDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            enquiry = EnquiryResponse.remap(b.enquiry),
            totalPrice = BigDecimal(0)
        )
      )
    )
}
