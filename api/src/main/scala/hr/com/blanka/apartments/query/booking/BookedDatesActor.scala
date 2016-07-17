package hr.com.blanka.apartments.query.booking

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.command.booking.EnquiryBooked

object BookedDatesActor {
  def apply() = Props(classOf[BookedDatesActor])

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e : EnquiryBooked => (e.bookingId.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case _ => "one"
  }
}

class BookedDatesActor extends Actor with ActorLogging {

  def receive: Receive = {
    case bs : EnquiryBooked =>
  }
}