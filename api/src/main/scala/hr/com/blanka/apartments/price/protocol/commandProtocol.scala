package hr.com.blanka.apartments.price.protocol

import org.joda.time.{DateTime, DateTimeZone}

/*
* Commands
*/

sealed trait Command {
  def unitId: Int
  def userId: String
}

case class SavePriceRange(userId: String, unitId: Int, from: Long, to: Long, price: Int) extends Command
case class SavePriceForSingleDay(userId: String, unitId: Int, day: Long, price: Int) extends Command

/*
* Events
*/

case class PriceRangeSaved(unitId: Int, from: Long, to: Long, price: Int)
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