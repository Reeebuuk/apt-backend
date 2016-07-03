package hr.com.blanka.apartments.query.price

import akka.actor.{Actor, Props}
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.command.price.{DailyPriceSaved, DayMonth}

object DailyPriceAggregateActor {
  def apply() = Props(classOf[DailyPriceAggregateActor])

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e @ DailyPriceSaved(userId, unitId, _, _, _) => (s"$userId$unitId", e)
    case e @ LookupPriceForDay(userId, unitId, _) => (s"$userId$unitId", e)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case e @ DailyPriceSaved(userId, unitId, _, _, _) => s"$userId$unitId"
    case e @ LookupPriceForDay(userId, unitId, _) => s"$userId$unitId"
  }
}

class DailyPriceAggregateActor extends Actor {

  def updateState(newDailyPrice: DailyPriceSaved, currentDailyPrices: Map[String, Map[Int, Map[DayMonth, Double]]]): Unit = {

    val newPrices : Map[String, Map[Int, Map[DayMonth, Double]]] = currentDailyPrices.get(newDailyPrice.userId) match {
      case None => currentDailyPrices + (newDailyPrice.userId -> Map(newDailyPrice.unitId -> Map(newDailyPrice.dayMonth -> newDailyPrice.price)))
      case Some(currentPricesForUser) => currentPricesForUser.get(newDailyPrice.unitId) match {
        case None => currentDailyPrices + (newDailyPrice.userId -> (currentPricesForUser ++ Map(newDailyPrice.unitId -> Map(newDailyPrice.dayMonth -> newDailyPrice.price))))
        case Some(currentPriceForUnit) =>
          currentDailyPrices + (newDailyPrice.userId -> (currentPricesForUser + (newDailyPrice.unitId -> (currentPriceForUnit + (newDailyPrice.dayMonth -> newDailyPrice.price)))))
      }
    }
    context become active(newPrices)
  }

  override def receive = active(Map[String, Map[Int, Map[DayMonth, Double]]]())

  def active(currentDailyPrices: Map[String, Map[Int, Map[DayMonth, Double]]]): Receive = {
    case e : DailyPriceSaved =>
      updateState(e, currentDailyPrices)

    case LookupPriceForDay(userId, unitId, day) =>
      val lastPrice: Double = currentDailyPrices.get(userId) match {
        case None => 0
        case Some(priceForUnit) => priceForUnit.get(unitId) match {
          case None => 0
          case Some(priceForDay) =>
            priceForDay.get(day) match {
              case None => 0
              case Some(price) => price
            }
        }

      }

      sender() ! PriceDayFetched(lastPrice)
  }

}
