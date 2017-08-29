package hr.com.blanka.apartments.command.booking

import java.time.{ LocalDate, LocalDateTime }

import hr.com.blanka.apartments.command.booking.Source.Source
import hr.com.blanka.apartments.common.ValueClasses.{ EnquiryId, UnitId, UserId }
import hr.com.blanka.apartments.common.Enquiry

/*
 * Commands
 */

sealed trait BookingCommand

sealed trait KnownBookingCommand extends BookingCommand {
  def enquiryId: EnquiryId
}

case class SaveEnquiryInitiated(userId: UserId, enquiry: Enquiry, source: Source)
    extends BookingCommand

case class SaveEnquiry(userId: UserId, enquiryId: EnquiryId, enquiry: Enquiry, source: Source)
    extends KnownBookingCommand
case class ApproveEnquiry(userId: UserId, enquiryId: EnquiryId) extends KnownBookingCommand
case class DepositPaid(userId: UserId, enquiryId: EnquiryId, amount: BigDecimal, currency: String)
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

case class NewEnquiryIdAssigned(enquiryId: EnquiryId)
case class EnquiryReceived(userId: UserId,
                           enquiryId: EnquiryId,
                           enquiry: Enquiry,
                           source: Source,
                           timeSaved: LocalDateTime)
case class EnquiryApproved(userId: UserId,
                           enquiryId: EnquiryId,
                           timeSaved: LocalDateTime,
                           unitId: UnitId,
                           dateFrom: LocalDate,
                           dateTo: LocalDate)
case class EnquiryBooked(userId: UserId,
                         enquiryId: EnquiryId,
                         timeSaved: LocalDateTime,
                         unitId: UnitId,
                         dateFrom: LocalDate,
                         dateTo: LocalDate,
                         depositAmount: BigDecimal,
                         depositCurrency: String)

object Source extends Enumeration {
  type Source = Value
  val Website, Airbnb, Apartmanija, Returning = Value

  def needsApproval(source: Source): Boolean = source == Website || source == Apartmanija
}
