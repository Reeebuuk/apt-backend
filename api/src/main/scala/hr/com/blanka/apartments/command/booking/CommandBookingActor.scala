package hr.com.blanka.apartments.command.booking

import akka.actor.{Actor, ActorLogging, Props}

object CommandBookingActor {
  def apply() = Props(classOf[CommandBookingActor])
}

class CommandBookingActor extends Actor with ActorLogging {

  val bookingAggregateActor = context.actorOf(Props(classOf[EnquiryAggregateActor]), "commandBookingAggregateActor")

  override def receive: Receive = {
    case sb : SaveEnquiry =>
      bookingAggregateActor ! sb
  }
}
