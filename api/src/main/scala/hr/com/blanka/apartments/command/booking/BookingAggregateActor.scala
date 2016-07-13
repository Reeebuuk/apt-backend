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
    case e @ SaveEnquiry(id, _) => (id.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case _ => "one"
  }

  val persistenceId = "BookingAggregateActor"
}

class BookingAggregateActor extends PersistentActor with ActorLogging {

  var entityId : Long = 0

  context become! state

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case SaveEnquiry(id, booking) => persist(EnquirySaved(booking, new DateTime())) {
      event => event
    }
  }

  override def persistenceId: String = BookingAggregateActor.persistenceId
}
