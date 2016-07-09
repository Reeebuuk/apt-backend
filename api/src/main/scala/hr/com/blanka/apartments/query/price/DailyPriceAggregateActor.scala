package hr.com.blanka.apartments.query.price

import akka.actor.{Actor, Props}
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.command.price.{DailyPriceSaved, DayMonth}

object DailyPriceAggregateActor {
  def apply() = Props(classOf[DailyPriceAggregateActor])

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e@DailyPriceSaved(userId, unitId, _, _, _) => (s"$userId$unitId", e)
    case e@LookupPriceForDay(userId, unitId, _) => (s"$userId$unitId", e)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case _ => "one"
  }
}

class DailyPriceAggregateActor extends Actor {

  def updateState(newDailyPrice: DailyPriceSaved, currentDailyPrices: Map[DayMonth, List[BigDecimal]]): Unit = {

    val newPrices: List[BigDecimal] = currentDailyPrices.get(newDailyPrice.dayMonth) match {
      case Some(priceForDay) => newDailyPrice.price :: priceForDay
      case None => List(newDailyPrice.price)
    }

    context become active(currentDailyPrices + (newDailyPrice.dayMonth -> newPrices))
  }

  override def receive = active(Map[DayMonth, List[BigDecimal]]())

  def active(currentDailyPrices: Map[DayMonth, List[BigDecimal]]): Receive = {
    case e: DailyPriceSaved =>
      updateState(e, currentDailyPrices)

    case LookupPriceForDay(userId, unitId, day) =>
      val lastPrice: BigDecimal = currentDailyPrices.get(day) match {
        case None => 0
        case Some(price) => price.head
      }

      sender() ! PriceDayFetched(lastPrice)
  }

}
