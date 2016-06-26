package hr.com.blanka.apartments.command.booking

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import org.joda.time.DateTime

object BookingAggregateActor {
  def apply() = Props(classOf[BookingAggregateActor])

  val persistenceId = "BookingAggregateActor"
}

class BookingAggregateActor extends PersistentActor with ActorLogging {
  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case SaveBooking(booking) => persist(BookingSaved(booking, new DateTime())) _
  }

  override def persistenceId: String = BookingAggregateActor.persistenceId
}
