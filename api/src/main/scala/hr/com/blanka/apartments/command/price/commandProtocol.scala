package hr.com.blanka.apartments.command.price

import org.joda.time.{DateTime, DateTimeZone}

/*
* Commands
*/

sealed trait PriceCommand {
  def unitId: Int
  def userId: String
}

case class SavePriceRange(userId: String, unitId: Int, from: Long, to: Long, price: Double) extends PriceCommand
case class SavePriceForSingleDay(userId: String, unitId: Int, day: DayMonth, price: Double) extends PriceCommand

/*
* Events
*/

case class PriceRangeSaved(userId: String, unitId: Int, from: Long, to: Long, price: Double)

case class DayMonth(day: Int, month: Long)
object DayMonth {
  def apply(day: Long) = {
    val date = new DateTime(day).toDateTime(DateTimeZone.UTC)
    new DayMonth(date.getDayOfMonth, date.getMonthOfYear)
  }
}

case class DailyPriceSaved(userId: String, unitId: Int, dayMonth: DayMonth, price: Double, dttm: DateTime)