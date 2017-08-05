package hr.com.blanka.apartments.command.price

import java.time.{ LocalDate, LocalDateTime }

import hr.com.blanka.apartments.common.DayMonth

/*
 * Commands
 */
sealed trait PriceCommand {
  def unitId: Int

  def userId: String
}

case class SavePriceRange(userId: String,
                          unitId: Int,
                          from: LocalDate,
                          to: LocalDate,
                          price: BigDecimal)
    extends PriceCommand

case class SavePriceForSingleDay(userId: String, unitId: Int, day: DayMonth, price: BigDecimal)
    extends PriceCommand

/*
 * Events
 */
case class PriceRangeSaved(userId: String,
                           unitId: Int,
                           from: LocalDate,
                           to: LocalDate,
                           price: BigDecimal)

case class DailyPriceSaved(userId: String,
                           unitId: Int,
                           dayMonth: DayMonth,
                           price: BigDecimal,
                           dttm: LocalDateTime)
