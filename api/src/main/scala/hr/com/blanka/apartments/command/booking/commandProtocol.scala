package hr.com.blanka.apartments.command.booking

import org.joda.time.DateTime

/*
* Commands
*/

sealed trait BookingCommand {
  def unitId: Int
  def userId: String
}

case class SaveEnquiry(enquiry: Enquiry) extends BookingCommand {
  override def unitId: Int = enquiry.unitId

  override def userId: String = enquiry.userId
}

/*
* Events
*/

case class EnquirySaved(enquiry: Enquiry, timeSaved: DateTime)


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