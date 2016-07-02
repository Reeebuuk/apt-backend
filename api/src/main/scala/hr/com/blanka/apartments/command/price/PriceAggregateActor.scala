package hr.com.blanka.apartments.command.price

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import org.joda.time.DateTime

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
      val msgSender = sender()
      persist(DailyPriceSaved(userId, unitId, day, price, new DateTime())) { event =>
        msgSender ! event
      }
  }

  override def persistenceId: String = PriceAggregateActor.persistenceId
}
