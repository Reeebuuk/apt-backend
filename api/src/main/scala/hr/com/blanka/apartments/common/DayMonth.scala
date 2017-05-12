package hr.com.blanka.apartments.common

import org.joda.time.LocalDate

case class DayMonth(day: Int, month: Long)
object DayMonth {
  def apply(day: LocalDate): DayMonth = {
    new DayMonth(day.getDayOfMonth, day.getMonthOfYear)
  }
}