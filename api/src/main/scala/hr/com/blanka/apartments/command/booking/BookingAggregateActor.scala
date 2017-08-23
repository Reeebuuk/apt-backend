package hr.com.blanka.apartments.command.booking

import java.time.LocalDateTime

import akka.actor.{ ActorLogging, Props }
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.common.Enquiry
import org.scalactic.Good

object BookingAggregateActor {
  def apply() = Props(classOf[BookingAggregateActor])

  def extractEntityId: ShardRegion.ExtractEntityId = {
    case e: KnownBookingCommand => (e.bookingId.id.toString, e)
  }

  def extractShardId: ShardRegion.ExtractShardId = _ => "one"

  val persistenceId = "BookingAggregateActor"
}

class BookingAggregateActor extends PersistentActor with ActorLogging {

  override def receiveCommand: Receive = init

  def init: Receive = {
    case SaveEnquiry(userId, bookingId, booking) =>
      persist(EnquirySaved(userId, bookingId, booking, LocalDateTime.now())) { e =>
        context become enquiry(e.enquiry)
        sender() ! Good(bookingId)
      }
    case e: MarkEnquiryAsBooked =>
      log.error(s"Received MarkEnquiryAsBooked for enquiry which doesn't exit $e")
  }

  def enquiry(enquiry: Enquiry): Receive = {
    case MarkEnquiryAsBooked(userId, bookingId, depositAmount, currency) =>
      persist(
        EnquiryBooked(userId, bookingId, enquiry, LocalDateTime.now(), depositAmount, currency)
      ) { e =>
        context become done(enquiry)
        sender() ! Good
      }
    case e: SaveEnquiry => log.error(s"Received SaveEnquiry with same Id $e")
  }

  def done(enquiry: Enquiry): Receive = {
    case _ =>
      log.info("Enquiry already booked")
      sender() ! Good
  }

  override def receiveRecover: Receive = {
    case EnquirySaved(_, bookingId, en, _) if bookingId.id.toString == context.self.path.name =>
      context become enquiry(en)
    case EnquiryBooked(_, bookingId, en, _, _, _)
        if bookingId.id.toString == context.self.path.name =>
      context become done(en)
  }

  override def persistenceId: String = BookingAggregateActor.persistenceId
}
