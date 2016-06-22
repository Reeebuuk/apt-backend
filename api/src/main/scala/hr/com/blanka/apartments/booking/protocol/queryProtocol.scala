package hr.com.blanka.apartments.booking.protocol

sealed trait BookingQuery

case class GetBookedDates(userId: String, unitId: Int) extends BookingQuery
case class GetAvailableApartments(userId: String, from: Long, to: Long) extends BookingQuery

sealed trait BookingQueryResponse

case class BookedDays(bookedDays: Set[BookedDay]) extends BookingQueryResponse
case class AvailableApartments(apartments: Set[Int]) extends BookingQueryResponse


case class BookedDay(date: String, firstDay: Boolean, lastDay: Boolean)
