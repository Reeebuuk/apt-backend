package hr.com.blanka.apartments.http.model

import java.time.{ LocalDate, ZoneOffset }

import hr.com.blanka.apartments.query.booking.{ AvailableUnits, BookedDay, BookedDays }
import hr.com.blanka.apartments.query.price.PricePerPeriod

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
