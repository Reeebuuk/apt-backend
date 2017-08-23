package hr.com.blanka.apartments.query.booking

import java.time.{ LocalDate, LocalDateTime }

import akka.actor.ActorRef
import hr.com.blanka.apartments.common.ValueClasses.{ BookingId, UnitId, UserId }
import hr.com.blanka.apartments.common.Enquiry

sealed trait BookingQuery

case class GetBookedDates(userId: UserId, unitId: UnitId)                    extends BookingQuery
case class GetAvailableUnits(userId: UserId, from: LocalDate, to: LocalDate) extends BookingQuery
case class GetAllBookings(userId: UserId)                                    extends BookingQuery

sealed trait BookingQueryResponse

case class BookedDays(bookedDays: List[BookedDay]) extends BookingQueryResponse
case class AvailableUnits(unitIds: Set[UnitId])    extends BookingQueryResponse
case class AllBookings(bookings: List[Booking])    extends BookingQueryResponse

case class BookedUnit(userId: UserId, unitId: UnitId, date: LocalDate, sequenceNumber: Long)

case class BookedDay(day: LocalDate, firstDay: Boolean, lastDay: Boolean)
case class StartSync(actor: ActorRef, persistenceId: String, initialIndex: Long)
case class BookingDeposit(amount: BigDecimal, currency: String, when: LocalDateTime)
case class Booking(bookingId: BookingId,
                   timeSaved: LocalDateTime,
                   enquiry: Enquiry,
                   bookingDeposit: Option[BookingDeposit] = None)
