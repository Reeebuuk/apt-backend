package hr.com.blanka.apartments.booking

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.booking.protocol.{BookingSaved, SaveBooking}
import org.joda.time.DateTime

case class Booking()

object BookingAggregateActor {
  def apply() = Props(classOf[CommandBookingActor])
}

class BookingAggregateActor extends PersistentActor with ActorLogging {
  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case SaveBooking(booking) => persist(BookingSaved(booking, new DateTime()))
  }

  override def persistenceId: String = BookingAggregateActor.toString
}
