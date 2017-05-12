package hr.com.blanka.apartments.command.booking

import org.joda.time.{ DateTime, LocalDate }

/*
 * Commands
 */

sealed trait BookingCommand

sealed trait KnownBookingCommand extends BookingCommand {
  def bookingId: Long
}

case class SaveEnquiryInitiated(userId: String, enquiry: Enquiry) extends BookingCommand

case class SaveEnquiry(userId: String, bookingId: Long, enquiry: Enquiry) extends KnownBookingCommand
case class DepositPaid(userId: String, bookingId: Long, depositAmount: BigDecimal, currency: String)
  extends KnownBookingCommand

case class MarkEnquiryAsBooked(userId: String, bookingId: Long, depositAmount: BigDecimal, currency: String)
  extends KnownBookingCommand

/*
 * Validation
 */

sealed trait ValidationQuery {
  def userId: String
}

case class CheckIfPeriodIsAvailable(userId: String, unitId: Int, dateFrom: LocalDate, dateTo: LocalDate)
  extends ValidationQuery

/*
 * Events
 */

case class NewBookingIdAssigned(bookingId: Long)
case class EnquirySaved(userId: String, bookingId: Long, enquiry: Enquiry, timeSaved: DateTime)
case class EnquiryBooked(
  userId: String,
  bookingId: Long,
  enquiry: Enquiry,
  timeSaved: DateTime,
  depositAmount: BigDecimal,
  currency: String
)

case class Enquiry(
  unitId: Int,
  dateFrom: LocalDate,
  dateTo: LocalDate,
  name: String,
  surname: String,
  phoneNumber: String,
  email: String,
  address: String,
  city: String,
  country: String,
  animals: String,
  noOfPeople: String,
  note: String
)
