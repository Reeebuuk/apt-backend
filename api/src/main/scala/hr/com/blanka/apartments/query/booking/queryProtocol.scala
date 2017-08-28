package hr.com.blanka.apartments.query.booking

import java.time.{ LocalDate, LocalDateTime }

import akka.actor.ActorRef
import hr.com.blanka.apartments.common.ValueClasses.{ BookingId, UnitId, UserId }
import hr.com.blanka.apartments.common.Enquiry

sealed trait BookingQuery

case class GetBookedDates(userId: UserId, unitId: UnitId)                    extends BookingQuery
case class GetAvailableUnits(userId: UserId, from: LocalDate, to: LocalDate) extends BookingQuery
case class GetAllUnapprovedEnquiries(userId: UserId, year: Int)              extends BookingQuery
case class GetAllApprovedEnquiries(userId: UserId, year: Int)                extends BookingQuery
case class GetAllBookings(userId: UserId, year: Int)                         extends BookingQuery

sealed trait BookingQueryResponse

case class BookedDays(bookedDays: List[BookedDay])                    extends BookingQueryResponse
case class AvailableUnits(unitIds: Set[UnitId])                       extends BookingQueryResponse
case class AllUnapprovedEnquiries(enquiries: List[UnapprovedEnquiry]) extends BookingQueryResponse
case class AllApprovedEnquiries(enquiries: List[ApprovedEnquiry])     extends BookingQueryResponse
case class AllBookings(bookings: List[Booking])                       extends BookingQueryResponse

case class BookedUnit(userId: UserId, unitId: UnitId, date: LocalDate, sequenceNumber: Long)

case class BookedDay(day: LocalDate, firstDay: Boolean, lastDay: Boolean)
case class StartSync(actor: ActorRef, persistenceId: String, initialIndex: Long)
case class BookingDeposit(amount: BigDecimal, currency: String, when: LocalDateTime)
case class Booking(bookingId: BookingId,
                   enquiryDttm: LocalDateTime,
                   enquiry: Enquiry,
                   approvedDttm: LocalDateTime,
                   bookingDeposit: BookingDeposit)
case class UnapprovedEnquiry(bookingId: BookingId, enquiryDttm: LocalDateTime, enquiry: Enquiry)
case class ApprovedEnquiry(bookingId: BookingId,
                           enquiryDttm: LocalDateTime,
                           enquiry: Enquiry,
                           approvedDttm: LocalDateTime)
