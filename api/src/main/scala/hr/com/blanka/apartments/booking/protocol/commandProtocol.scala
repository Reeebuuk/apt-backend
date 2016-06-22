package hr.com.blanka.apartments.booking.protocol

import org.joda.time.DateTime

/*
* Commands
*/

sealed trait Command {
  def unitId: Int
  def userId: String
}

case class SaveBooking(booking: Booking) extends Command {
  override def unitId: Int = booking.unitId

  override def userId: String = booking.userId
}

/*
* Events
*/

case class BookingSaved(booking: Booking, timeSaved: DateTime)


case class Booking(userId: String,
                   unitId: Int,
                   dateFrom: Long,
                   dateTo: Long,
                   depositPaid: Boolean,
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