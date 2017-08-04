package hr.com.blanka.apartments.command.price

import java.time.LocalDateTime

import akka.actor.{ ActorLogging, Props }
import akka.persistence.PersistentActor

object PriceAggregateActor {
  def apply() = Props(classOf[PriceAggregateActor])

  val persistenceId = "PriceAggregateActor"
}

class PriceAggregateActor extends PersistentActor with ActorLogging {
  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case SavePriceForSingleDay(userId, unitId, day, price) =>
      persist(DailyPriceSaved(userId, unitId, day, price, LocalDateTime.now())) { event =>
        sender() ! event
      }
  }

  override def persistenceId: String = PriceAggregateActor.persistenceId
}
