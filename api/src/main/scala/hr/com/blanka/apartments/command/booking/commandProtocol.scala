package hr.com.blanka.apartments.command.booking

import org.joda.time.DateTime

/*
* API
*/

case class EnquiryReceived(enquiry: Enquiry)

/*
* Commands
*/

sealed trait BookingCommand {
  def unitId: Int
  def userId: String
}

case class SaveEnquiry(id: Long, enquiry: Enquiry) extends BookingCommand {
  override def unitId: Int = enquiry.unitId

  override def userId: String = enquiry.userId
}

/*
* Events
*/

case class NewBookingIdAssigned(id: Long)
case class EnquirySaved(id: Long, enquiry: Enquiry, timeSaved: DateTime)


case class Enquiry(userId: String,
                   unitId: Int,
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