package hr.com.blanka.apartments.command.booking

import org.joda.time.DateTime

/*
* API
*/

case class EnquiryReceived(userId: String, enquiry: Enquiry)
case class DepositPaid(userId: String, bookingId: Long, depositAmount: BigDecimal, currency: String)

/*
* Commands
*/

sealed trait BookingCommand {
  def bookingId: Long
}

case class SaveEnquiry(userId: String, bookingId: Long, enquiry: Enquiry) extends BookingCommand

case class MarkEnquiryAsBooked(userId: String, bookingId: Long, depositAmount: BigDecimal, currency: String) extends BookingCommand


/*
 * Validation
 */

sealed trait ValidationQuery {
  def userId: String
}

case class CheckIfPeriodIsAvailable(userId: String, unitId: Int, dateFrom: Long, dateTo: Long) extends ValidationQuery

/*
* Events
*/

case class NewBookingIdAssigned(bookingId: Long)
case class EnquirySaved(userId: String, bookingId: Long, enquiry: Enquiry, timeSaved: DateTime)
case class EnquiryBooked(userId: String, bookingId: Long, enquiry: Enquiry, timeSaved: DateTime, depositAmount: BigDecimal, currency: String)

case class Enquiry(unitId: Int,
                   dateFrom: Long,
                   dateTo: Long,
                   name: String,
                   surname: String,
                   phoneNumber: String,
                   email: String,
                   address: String,
                   city: String,
                   country: String,
                   animals: String,
                   noOfPeople: String,
                   note: String)