package hr.com.blanka.apartments.command.booking

import java.time.{ LocalDate, LocalDateTime }

import hr.com.blanka.apartments.command.booking.Source.Source
import hr.com.blanka.apartments.common.ValueClasses.{ BookingId, UnitId, UserId }
import hr.com.blanka.apartments.common.Enquiry

/*
 * Commands
 */

sealed trait BookingCommand

sealed trait KnownBookingCommand extends BookingCommand {
  def bookingId: BookingId
}

case class SaveEnquiryInitiated(userId: UserId, enquiry: Enquiry, source: Source)
    extends BookingCommand

case class SaveEnquiry(userId: UserId, bookingId: BookingId, enquiry: Enquiry, source: Source)
    extends KnownBookingCommand
case class ApproveEnquiry(userId: UserId, bookingId: BookingId) extends KnownBookingCommand
case class DepositPaid(userId: UserId,
                       bookingId: BookingId,
                       depositAmount: BigDecimal,
                       currency: String)
    extends KnownBookingCommand

/*
 * Validation
 */

sealed trait ValidationQuery {
  def userId: UserId
}

/*
 * Events
 */

case class NewBookingIdAssigned(bookingId: BookingId)
case class EnquiryReceived(userId: UserId,
                           bookingId: BookingId,
                           enquiry: Enquiry,
                           source: Source,
                           timeSaved: LocalDateTime)
case class EnquiryApproved(userId: UserId,
                           bookingId: BookingId,
                           timeSaved: LocalDateTime,
                           unitId: UnitId,
                           dateFrom: LocalDate,
                           dateTo: LocalDate)
case class EnquiryBooked(userId: UserId,
                         bookingId: BookingId,
                         timeSaved: LocalDateTime,
                         unitId: UnitId,
                         dateFrom: LocalDate,
                         dateTo: LocalDate,
                         depositAmount: BigDecimal,
                         currency: String)

object Source extends Enumeration {
  type Source = Value
  val Website, Airbnb, Apartmanija, Returning = Value

  def needsApproval(source: Source): Boolean = source == Website || source == Apartmanija
}
