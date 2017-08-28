package hr.com.blanka.apartments.http.model

import java.time.{ LocalDate, ZoneOffset }

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

case class NewEnquiryResponse(bookingId: Long)

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

case class AllBookingsResponse(bookings: List[BookingResponse])
case class BookingResponse(bookingId: Long,
                           enquiryDttm: Long,
                           approvalDttm: Long,
                           unitId: Int,
                           dateFrom: Long,
                           dateTo: Long,
                           name: String,
                           surname: String,
                           phoneNumber: String,
                           email: String,
                           address: String,
                           city: String,
                           country: String,
                           animals: String,
                           noOfPeople: String,
                           note: String,
                           totalPrice: BigDecimal,
                           depositAmount: BigDecimal,
                           depositCurrency: String,
                           depositWhen: Long)

object AllBookingsResponse {
  def remap(allBookings: AllBookings): AllBookingsResponse =
    AllBookingsResponse(
      allBookings.bookings.map(
        b =>
          BookingResponse(
            bookingId = b.bookingId.id,
            enquiryDttm = b.enquiryDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            approvalDttm = b.approvedDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            unitId = b.enquiry.unitId.id,
            dateFrom = b.enquiry.dateFrom.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
            dateTo = b.enquiry.dateTo.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
            name = b.enquiry.name,
            surname = b.enquiry.surname,
            phoneNumber = b.enquiry.phoneNumber,
            email = b.enquiry.email,
            address = b.enquiry.address,
            city = b.enquiry.city,
            country = b.enquiry.country,
            animals = b.enquiry.animals,
            noOfPeople = b.enquiry.noOfPeople,
            note = b.enquiry.note,
            totalPrice = BigDecimal.valueOf(0),
            depositAmount = b.bookingDeposit.amount,
            depositCurrency = b.bookingDeposit.currency,
            depositWhen = b.bookingDeposit.when.toInstant(ZoneOffset.UTC).toEpochMilli
        )
      )
    )
}

case class AllApprovedEnquiriesResponse(enquiries: List[ApprovedEnquiryResponse])
case class ApprovedEnquiryResponse(bookingId: Long,
                                   enquiryDttm: Long,
                                   approvalDttm: Long,
                                   unitId: Int,
                                   dateFrom: Long,
                                   dateTo: Long,
                                   name: String,
                                   surname: String,
                                   phoneNumber: String,
                                   email: String,
                                   address: String,
                                   city: String,
                                   country: String,
                                   animals: String,
                                   noOfPeople: String,
                                   note: String,
                                   totalPrice: BigDecimal)

object AllApprovedEnquiriesResponse {
  def remap(allApprovedEnquiries: AllApprovedEnquiries): AllApprovedEnquiriesResponse =
    AllApprovedEnquiriesResponse(
      allApprovedEnquiries.enquiries.map(
        b =>
          ApprovedEnquiryResponse(
            bookingId = b.bookingId.id,
            enquiryDttm = b.enquiryDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            approvalDttm = b.approvedDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            unitId = b.enquiry.unitId.id,
            dateFrom = b.enquiry.dateFrom.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
            dateTo = b.enquiry.dateTo.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
            name = b.enquiry.name,
            surname = b.enquiry.surname,
            phoneNumber = b.enquiry.phoneNumber,
            email = b.enquiry.email,
            address = b.enquiry.address,
            city = b.enquiry.city,
            country = b.enquiry.country,
            animals = b.enquiry.animals,
            noOfPeople = b.enquiry.noOfPeople,
            note = b.enquiry.note,
            totalPrice = BigDecimal.valueOf(0)
        )
      )
    )
}

case class AllUnapprovedEnquiriesResponse(enquiries: List[UnapprovedEnquiryResponse])
case class UnapprovedEnquiryResponse(bookingId: Long,
                                     enquiryDttm: Long,
                                     unitId: Int,
                                     dateFrom: Long,
                                     dateTo: Long,
                                     name: String,
                                     surname: String,
                                     phoneNumber: String,
                                     email: String,
                                     address: String,
                                     city: String,
                                     country: String,
                                     animals: String,
                                     noOfPeople: String,
                                     note: String,
                                     totalPrice: BigDecimal)

object AllUnapprovedEnquiriesResponse {
  def remap(allUnapprovedEnquiries: AllUnapprovedEnquiries): AllUnapprovedEnquiriesResponse =
    AllUnapprovedEnquiriesResponse(
      allUnapprovedEnquiries.enquiries.map(
        b =>
          UnapprovedEnquiryResponse(
            bookingId = b.bookingId.id,
            enquiryDttm = b.enquiryDttm.toInstant(ZoneOffset.UTC).toEpochMilli,
            unitId = b.enquiry.unitId.id,
            dateFrom = b.enquiry.dateFrom.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
            dateTo = b.enquiry.dateTo.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
            name = b.enquiry.name,
            surname = b.enquiry.surname,
            phoneNumber = b.enquiry.phoneNumber,
            email = b.enquiry.email,
            address = b.enquiry.address,
            city = b.enquiry.city,
            country = b.enquiry.country,
            animals = b.enquiry.animals,
            noOfPeople = b.enquiry.noOfPeople,
            note = b.enquiry.note,
            totalPrice = BigDecimal.valueOf(0)
        )
      )
    )
}
