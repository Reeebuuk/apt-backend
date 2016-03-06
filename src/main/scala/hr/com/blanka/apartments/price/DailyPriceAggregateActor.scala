package hr.com.blanka.apartments.price

import akka.actor.Props
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.price.protocol.{LookupPriceForDay, PriceDayFetched, SavePriceForSingleDay}
import org.joda.time.DateTime
import org.scalactic.Good

case class Price(value: Int, timeSaved: Long)

object Price {
  def apply(value: Int) = new Price(value, DateTime.now().getMillis)
}

case class DailyPriceSaved(unitId: Int, day: Long, newPrice: Price)

object DailyPriceAggregateActor {

  def apply() = Props(classOf[DailyPriceAggregateActor])

}

class DailyPriceAggregateActor extends PersistentActor {

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  def updateState(newDailyPrice: DailyPriceSaved, currentDailyPrices: Map[Int, Map[Long, List[Price]]]): Unit = {
    import newDailyPrice._

    val newDailyPrices: Map[Long, List[Price]] = currentDailyPrices.get(unitId) match {
      case None => Map(day -> List(newPrice))
      case Some(previousPricesForDay) =>
        previousPricesForDay.get(day) match {
          case None => Map(day -> List(newPrice))
          case Some(previousPrices) => Map(day -> previousPrices.+:(newPrice))
        }
    }

    context become active(currentDailyPrices + (unitId -> newDailyPrices))
    sender() ! Good("Saved")
  }

  override def receiveCommand = active(Map[Int, Map[Long, List[Price]]]())

  def active(currentDailyPrices: Map[Int, Map[Long, List[Price]]]): Receive = {
    case SavePriceForSingleDay(userId, unitId, day, price) => {
      persist(DailyPriceSaved(unitId, day, Price(price)))(updateState(_, currentDailyPrices))
    }
    case LookupPriceForDay(_, unitId, day) => {
      val lastPrice: Int = currentDailyPrices.get(unitId) match {
        case None => 0
        case Some(priceForDay) =>
          priceForDay.get(day) match {
            case None => 0
            case Some(prices) => prices.head.value
          }
      }

      sender() ! PriceDayFetched(lastPrice)
    }
  }

  override def receiveRecover: Receive = {
    case _ =>
  }
}
