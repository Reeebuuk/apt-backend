package hr.com.blanka.apartments.query.price

import akka.actor.{ Actor, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.command.price.{ DailyPriceSaved, PriceAggregateActor }
import hr.com.blanka.apartments.common.DayMonth
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import hr.com.blanka.apartments.query.booking.StartSync

object DailyPriceAggregateActor {
  def apply(parent: ActorRef) = Props(classOf[DailyPriceAggregateActor], parent)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e @ DailyPriceSaved(userId, unitId, _, _, _) => (s"${userId.id}${unitId.id}", e)
    case e @ LookupPriceForDay(userId, unitId, _)     => (s"${userId.id}${unitId.id}", e)
  }

  val extractShardId: ShardRegion.ExtractShardId = _ => "one"

  def updateState(
      newDailyPrice: DailyPriceSaved,
      currentDailyPrices: Map[DayMonth, List[BigDecimal]]
  ): Map[DayMonth, List[BigDecimal]] = {

    val newPrices: List[BigDecimal] = currentDailyPrices.get(newDailyPrice.dayMonth) match {
      case Some(priceForDay) => newDailyPrice.price :: priceForDay
      case None              => List(newDailyPrice.price)
    }

    currentDailyPrices + (newDailyPrice.dayMonth -> newPrices)
  }

  def fetchLatestPriceForDay(currentDailyPrices: Map[DayMonth, List[BigDecimal]],
                             day: DayMonth): BigDecimal =
    currentDailyPrices.get(day) match {
      case None        => 0
      case Some(price) => price.head
    }
}

class DailyPriceAggregateActor(parent: ActorRef) extends Actor {

  import DailyPriceAggregateActor._

  override def receive: Receive = active(Map.empty)

  def active(currentDailyPrices: Map[DayMonth, List[BigDecimal]]): Receive = {
    case PersistenceQueryEvent(_, e: DailyPriceSaved) =>
      context become active(updateState(e, currentDailyPrices))

    case LookupPriceForDay(_, _, day) =>
      sender() ! PriceDayFetched(fetchLatestPriceForDay(currentDailyPrices, day))
  }

  override def preStart(): Unit = {
    parent ! StartSync(self, PriceAggregateActor.persistenceId, 0)
    super.preStart()
  }
}
