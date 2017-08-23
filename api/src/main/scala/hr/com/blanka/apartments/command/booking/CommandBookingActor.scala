package hr.com.blanka.apartments.command.booking

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.ValueClasses.BookingId
import hr.com.blanka.apartments.utils.PredefinedTimeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object CommandBookingActor {
  def apply() = Props(classOf[CommandBookingActor])
}

class CommandBookingActor extends PersistentActor with ActorLogging with PredefinedTimeout {

  val bookingAggregateActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "commandBookingAggregateActor",
    entityProps = BookingAggregateActor(),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = BookingAggregateActor.extractEntityId,
    extractShardId = BookingAggregateActor.extractShardId
  )

  var bookingCounter: Long = 0l

  override def receiveCommand: Receive = {
    case SaveEnquiryInitiated(userId, enquiry) =>
      persist(NewBookingIdAssigned(BookingId(bookingCounter + 1))) { event =>
        bookingCounter = event.bookingId.id
        bookingAggregateActor ? SaveEnquiry(userId, event.bookingId, enquiry) pipeTo sender()
      }
    case DepositPaid(userId, bookingId, depositAmount, currency) =>
      bookingAggregateActor ? MarkEnquiryAsBooked(userId, bookingId, depositAmount, currency) pipeTo sender()
  }

  override def receiveRecover: Receive = {
    case NewBookingIdAssigned(counter) => bookingCounter = counter.id
  }

  override def persistenceId: String = "BookingCounter"
}
