package hr.com.blanka.apartments.common

import java.time.LocalDate

case class DayMonth(day: Int, month: Int)
object DayMonth {
  def apply(day: LocalDate): DayMonth =
    new DayMonth(day.getDayOfMonth, day.getMonthValue)
}
