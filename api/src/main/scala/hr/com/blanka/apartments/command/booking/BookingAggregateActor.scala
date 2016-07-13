package hr.com.blanka.apartments.command.booking

import akka.actor.{ActorLogging, Props}
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.command.price.DailyPriceSaved
import hr.com.blanka.apartments.query.price.LookupPriceForDay
import org.joda.time.DateTime

object BookingAggregateActor {
  def apply() = Props(classOf[BookingAggregateActor])

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e : BookingCommand => (e.bookingId.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case _ => "one"
  }

  val persistenceId = "BookingAggregateActor"
}

class BookingAggregateActor extends PersistentActor with ActorLogging {

  var entityId : Long = 0

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = init

  var enquiryValue: Enquiry = _

  def init: Receive = {
    case SaveEnquiry(userId, id, booking) => persist(EnquirySaved(userId, id, booking, new DateTime())) {
      e =>
        enquiryValue = e.enquiry
        context become enquiry
    }
    case e: MarkEnquiryAsBooked => log.error(s"Received MarkEnquiryAsBooked for enquiry which doesn't exit $e")
  }

  def enquiry : Receive = {
    case MarkEnquiryAsBooked(userId, id) => persist(EnquiryBooked(userId, id, enquiryValue, new DateTime())) {
      e => context become booking
    }
    case e: SaveEnquiry => log.error(s"Received SaveEnquiry with same Id $e")
  }

  def booking: Receive ={
    case _ =>
  }

  override def persistenceId: String = BookingAggregateActor.persistenceId
}
