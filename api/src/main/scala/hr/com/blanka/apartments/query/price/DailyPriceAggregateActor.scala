package hr.com.blanka.apartments.query.price

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.command.price.DailyPriceSaved
import hr.com.blanka.apartments.common.DayMonth
import hr.com.blanka.apartments.query.PersistenceQueryEvent

object DailyPriceAggregateActor {
  def apply(parent: ActorRef) = Props(classOf[DailyPriceAggregateActor], parent)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case PersistenceQueryEvent(_, e: DailyPriceSaved) => {
      (s"${e.userId.id}${e.unitId.id}", e)
    }
    case e @ LookupPriceForDay(userId, unitId, _, _) => {
      val lala = userId
      (s"${userId.id}${unitId.id}", e)
    }
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

class DailyPriceAggregateActor(parent: ActorRef) extends Actor with ActorLogging {

  import DailyPriceAggregateActor._

  override def receive: Receive = active(Map.empty)

  def active(currentDailyPrices: Map[DayMonth, List[BigDecimal]]): Receive = {
    case e: DailyPriceSaved =>
      context become active(updateState(e, currentDailyPrices))

    case LookupPriceForDay(_, _, day, _) =>
      sender() ! PriceDayFetched(fetchLatestPriceForDay(currentDailyPrices, day))
  }
}
