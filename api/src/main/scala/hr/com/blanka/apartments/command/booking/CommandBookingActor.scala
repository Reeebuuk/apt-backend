package hr.com.blanka.apartments.command.booking

import akka.actor.{Actor, ActorLogging, Props}

object CommandBookingActor {
  def apply() = Props(classOf[CommandBookingActor])
}

class CommandBookingActor extends Actor with ActorLogging {

  val bookingAggregateActor = context.actorOf(Props(classOf[BookingAggregateActor]), "commandBookingAggregateActor")

  override def receive: Receive = {
    case sb : SaveBooking =>
      bookingAggregateActor ! sb
  }
}
