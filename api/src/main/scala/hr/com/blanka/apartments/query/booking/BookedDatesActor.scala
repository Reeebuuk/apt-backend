package hr.com.blanka.apartments.query.booking

import akka.actor.{Actor, ActorLogging, Props}
import hr.com.blanka.apartments.command.booking.BookingSaved

object BookedDatesActor {
  def apply() = Props(classOf[BookedDatesActor])
}

class BookedDatesActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case bs : BookingSaved =>
  }
}