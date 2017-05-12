package hr.com.blanka.apartments.http.model

import hr.com.blanka.apartments.query.booking.{ AvailableApartments, BookedDay, BookedDays }
import org.joda.time.LocalDate

case class PriceForRangeResponse(price: BigDecimal)

case class ErrorResponse(msg: String)

case class BookedDaysResponse(bookedDays: List[BookedDayResponse])
object BookedDaysResponse {
  def remap(bd: BookedDays): BookedDaysResponse =
    BookedDaysResponse(bd.bookedDays.map(BookedDayResponse.remap))
}

case class AvailableApartmentsResponse(apartments: Set[Int])
object AvailableApartmentsResponse {
  def remap(aa: AvailableApartments): AvailableApartmentsResponse =
    AvailableApartmentsResponse(aa.apartmentIds)
}

case class BookedDayResponse(day: LocalDate, firstDay: Boolean, lastDay: Boolean)
object BookedDayResponse {
  def remap(bd: BookedDay): BookedDayResponse =
    BookedDayResponse(bd.day, bd.firstDay, bd.lastDay)
}
