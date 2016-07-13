package hr.com.blanka.apartments.command.booking

import akka.actor.{ActorLogging, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.persistence.PersistentActor

object CommandBookingActor {
  def apply() = Props(classOf[CommandBookingActor])
}

class CommandBookingActor extends PersistentActor with ActorLogging {

  val bookingAggregateActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "commandBookingAggregateActor",
    entityProps = BookingAggregateActor(),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = BookingAggregateActor.extractEntityId,
    extractShardId = BookingAggregateActor.extractShardId)

  var bookingCounter: Long = 0l

  override def receiveRecover: Receive = {
    case sb : EnquiryReceived =>
      persist(NewBookingIdAssigned(bookingCounter + 1)) { event =>
        bookingCounter = event.id
        bookingAggregateActor ! SaveEnquiry(bookingCounter, sb.enquiry)
      }
  }

  override def receiveCommand: Receive = {
    case NewBookingIdAssigned(counter) => bookingCounter = counter
  }

  override def persistenceId: String = "BookingCounter"
}
