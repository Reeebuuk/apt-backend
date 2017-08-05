package hr.com.blanka.apartments.command.booking

import java.time.LocalDateTime

import akka.actor.{ ActorLogging, Props }
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import org.scalactic.Good

object BookingAggregateActor {
  def apply() = Props(classOf[BookingAggregateActor])

  def extractEntityId: ShardRegion.ExtractEntityId = {
    case e: KnownBookingCommand => (e.bookingId.toString, e)
  }

  def extractShardId: ShardRegion.ExtractShardId = _ => "one"

  val persistenceId = "BookingAggregateActor"
}

class BookingAggregateActor extends PersistentActor with ActorLogging {

  var entityId: Long = 0

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = init

  var enquiryValue: Enquiry = _

  def init: Receive = {
    case SaveEnquiry(userId, id, booking) =>
      persist(EnquirySaved(userId, id, booking, LocalDateTime.now())) { e =>
        enquiryValue = e.enquiry
        context become enquiry
        sender() ! Good
      }
    case e: MarkEnquiryAsBooked =>
      log.error(s"Received MarkEnquiryAsBooked for enquiry which doesn't exit $e")
  }

  def enquiry: Receive = {
    case MarkEnquiryAsBooked(userId, bookingId, depositAmount, currency) =>
      persist(
        EnquiryBooked(userId, bookingId, enquiryValue, LocalDateTime.now(), depositAmount, currency)
      ) { e =>
        sender() ! Good
      }
    case e: SaveEnquiry => log.error(s"Received SaveEnquiry with same Id $e")
  }

  def booking: Receive = {
    case _ =>
  }

  override def persistenceId: String = BookingAggregateActor.persistenceId
}
