package hr.com.blanka.apartments.booking

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import hr.com.blanka.apartments.booking.protocol.SaveBooking

object CommandBookingActor {
  def apply(bookingAggregateActor: ActorRef) = Props(classOf[CommandBookingActor], bookingAggregateActor)
}

class CommandBookingActor(bookingAggregateActor: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case sb: SaveBooking =>
      //validate
      bookingAggregateActor ! sb
  }
}
