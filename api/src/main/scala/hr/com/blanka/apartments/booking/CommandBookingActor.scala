package hr.com.blanka.apartments.booking

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.booking.protocol.{BookingSaved, SaveBooking}

object CommandBookingActor {
  def apply(bookingAggregateActor: ActorRef) = Props(classOf[CommandBookingActor], bookingAggregateActor)

  val persistenceId = "CommandBookingActor"
}

class CommandBookingActor(bookingAggregateActor: ActorRef) extends PersistentActor with ActorLogging {

  override def persistenceId: String = CommandBookingActor.persistenceId

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case bs : BookingSaved =>
      persist(bs)
  }
}
