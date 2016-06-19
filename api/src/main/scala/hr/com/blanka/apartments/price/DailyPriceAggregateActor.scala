package hr.com.blanka.apartments.price

import akka.actor.{Actor, Props}
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.price.protocol.{LookupPriceForDay, PriceDayFetched, SavePriceForSingleDay}
import org.joda.time.{DateTime, DateTimeZone}

import scala.annotation.tailrec

case class Price(value: Int, timeSaved: Long)
object Price {
  def apply(value: Int) = new Price(value, DateTime.now().getMillis)
}

case class DayMonth(day: Int, month: Long)
object DayMonth {
  def apply(day: Long) = {
    val date = new DateTime(day).toDateTime(DateTimeZone.UTC)
    new DayMonth(date.getDayOfMonth, date.getMonthOfYear)
  }
}

case class DailyPriceSaved(unitId: Int, dayMonth: DayMonth, newPrice: Price)
object DailyPriceSaved {
  def apply(unitId: Int, day: Long, price: Int) =
    new DailyPriceSaved(unitId, DayMonth(day), Price(price))
}

object DailyPriceAggregateActor {
  def apply() = Props(classOf[DailyPriceAggregateActor])
}

class DailyPriceAggregateActor extends Actor {
  //  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  def updateState(newDailyPrice: DailyPriceSaved, currentDailyPrices: Map[Int, Map[DayMonth, List[Price]]]): Unit = {
    import newDailyPrice._

    val newDailyPrices: Map[DayMonth, List[Price]] = currentDailyPrices.get(unitId) match {
      case None => Map(dayMonth -> List(newPrice))
      case Some(currentPriceForDayMonth) =>
        val newPriceForDayMonth = currentPriceForDayMonth.get(dayMonth) match {
          case None => Map(dayMonth -> List(newPrice))
          case Some(previousPrices) => Map(dayMonth -> (newPrice :: previousPrices))
        }
        currentPriceForDayMonth ++ newPriceForDayMonth
    }

    context become active(currentDailyPrices + (unitId -> newDailyPrices))
  }

  override def receive = active(Map[Int, Map[DayMonth, List[Price]]]())

  def active(currentDailyPrices: Map[Int, Map[DayMonth, List[Price]]]): Receive = {
    case SavePriceForSingleDay(userId, unitId, day, price) =>
      //      persist(DailyPriceSaved(unitId, day, Price(price)))(updateState(_, currentDailyPrices))
      updateState(DailyPriceSaved(unitId, day, price), currentDailyPrices)

    case LookupPriceForDay(_, unitId, day) =>
      val lastPrice: Int = currentDailyPrices.get(unitId) match {
        case None => 0
        case Some(priceForDay) =>
          priceForDay.get(DayMonth(day)) match {
            case None => 0
            case Some(price :: Nil) => price.value
            case Some(prices) => findValidPrice(prices, day)
          }
      }

      sender() ! PriceDayFetched(lastPrice)
  }

  @tailrec
  private def findValidPrice(prices: List[Price], day: Long): Int = prices match {
    case price :: Nil => price.value
    case price1 :: price2 :: _ if price1.timeSaved > day && price2.timeSaved < day => price1.value
    case _ :: rest => findValidPrice(rest, day)
  }

  //  override def receiveRecover: Receive = {
  //    case _ =>
  //  }
}
