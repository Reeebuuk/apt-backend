package hr.com.blanka.apartments.command.booking

import org.joda.time.DateTime

/*
* API
*/

case class EnquiryReceived(userId: String, enquiry: Enquiry)

/*
* Commands
*/

sealed trait BookingCommand {
  def bookingId: Long
}

case class SaveEnquiry(userId: String, bookingId: Long, enquiry: Enquiry) extends BookingCommand

case class MarkEnquiryAsBooked(userId: String, bookingId: Long) extends BookingCommand

/*
* Events
*/

case class NewBookingIdAssigned(id: Long)
case class EnquirySaved(userId: String, id: Long, enquiry: Enquiry, timeSaved: DateTime)
case class EnquiryBooked(userId: String, id: Long, enquiry: Enquiry, timeSaved: DateTime)

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