package hr.com.blanka.apartments.http.model

import hr.com.blanka.apartments.query.booking.{ AvailableUnits, BookedDay, BookedDays }
import java.time.LocalDate

case class PriceForRangeResponse(price: BigDecimal)

case class ErrorResponse(msg: String)

case class BookedDaysResponse(bookedDays: List[BookedDayResponse])
object BookedDaysResponse {
  def remap(bd: BookedDays): BookedDaysResponse =
    BookedDaysResponse(bd.bookedDays.map(BookedDayResponse.remap))
}

case class AvailableUnitsResponse(apartments: Set[Int])
object AvailableUnitsResponse {
  def remap(aa: AvailableUnits): AvailableUnitsResponse =
    new AvailableUnitsResponse(aa.unitIds.map(_.id))
}

case class BookedDayResponse(day: LocalDate, firstDay: Boolean, lastDay: Boolean)
object BookedDayResponse {
  def remap(bd: BookedDay): BookedDayResponse =
    BookedDayResponse(bd.day, bd.firstDay, bd.lastDay)
}

case class NewEnquiryResponse(bookingId: Long)
