package hr.com.blanka.apartments.command.booking

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import akka.persistence.PersistentActor
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object CommandBookingActor {
  def apply() = Props(classOf[CommandBookingActor])
}

class CommandBookingActor extends PersistentActor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val bookingAggregateActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "commandBookingAggregateActor",
    entityProps = BookingAggregateActor(),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = BookingAggregateActor.extractEntityId,
    extractShardId = BookingAggregateActor.extractShardId
  )

  var bookingCounter: Long = 0l

  override def receiveCommand: Receive = {
    case EnquiryReceived(userId, enquiry) =>
      persist(NewBookingIdAssigned(bookingCounter + 1)) { event =>
        bookingCounter = event.bookingId
        bookingAggregateActor ? SaveEnquiry(userId, bookingCounter, enquiry) pipeTo sender()
      }
    case DepositPaid(userId, bookingId, depositAmount, currency) =>
      bookingAggregateActor ? MarkEnquiryAsBooked(userId, bookingId, depositAmount, currency) pipeTo sender()
  }

  override def receiveRecover: Receive = {
    case NewBookingIdAssigned(counter) => bookingCounter = counter
  }

  override def persistenceId: String = "BookingCounter"
}
