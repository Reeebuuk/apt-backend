package hr.com.blanka.apartments.query.booking

import akka.actor.{Actor, ActorLogging, Props}
import hr.com.blanka.apartments.command.booking.BookingSaved

object UnitAvailabilityActor {
  def apply() = Props(classOf[UnitAvailabilityActor])
}

class UnitAvailabilityActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case bs : BookingSaved =>
  }
}
