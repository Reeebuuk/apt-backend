package hr.com.blanka.apartments.command.booking

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.common.ValueClasses.EnquiryId
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

  override def receiveCommand: Receive = receive(0l)

  def receive(bookingCounter: Long): Receive = {
    case SaveEnquiryInitiated(userId, enquiry, source) =>
      persist(NewEnquiryIdAssigned(EnquiryId(bookingCounter + 1))) { event =>
        context become receive(event.enquiryId.id)
        bookingAggregateActor ? SaveEnquiry(userId, event.enquiryId, enquiry, source) pipeTo sender()
      }
    case e @ (_: DepositPaid | _: ApproveEnquiry) =>
      bookingAggregateActor ? e pipeTo sender()
  }

  override def receiveRecover: Receive = {
    case NewEnquiryIdAssigned(counter) =>
      context become receive(counter.id)
  }

  override def persistenceId: String = "BookingCounter"
}
