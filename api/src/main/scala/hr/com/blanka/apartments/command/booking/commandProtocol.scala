package hr.com.blanka.apartments.command.booking

import java.time.{ LocalDate, LocalDateTime }

import hr.com.blanka.apartments.ValueClasses.{ BookingId, UnitId, UserId }
import hr.com.blanka.apartments.common.Enquiry

/*
 * Commands
 */

sealed trait BookingCommand

sealed trait KnownBookingCommand extends BookingCommand {
  def bookingId: BookingId
}

case class SaveEnquiryInitiated(userId: UserId, enquiry: Enquiry) extends BookingCommand

case class SaveEnquiry(userId: UserId, bookingId: BookingId, enquiry: Enquiry)
    extends KnownBookingCommand
case class DepositPaid(userId: UserId,
                       bookingId: BookingId,
                       depositAmount: BigDecimal,
                       currency: String)
    extends KnownBookingCommand

case class MarkEnquiryAsBooked(userId: UserId,
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
case class EnquirySaved(userId: UserId,
                        bookingId: BookingId,
                        enquiry: Enquiry,
                        timeSaved: LocalDateTime)
case class EnquiryBooked(userId: UserId,
                         bookingId: BookingId,
                         enquiry: Enquiry,
                         timeSaved: LocalDateTime,
                         depositAmount: BigDecimal,
                         currency: String)
