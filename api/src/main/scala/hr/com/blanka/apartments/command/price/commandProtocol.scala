package hr.com.blanka.apartments.command.price

import java.time.{ LocalDate, LocalDateTime }

import hr.com.blanka.apartments.ValueClasses.{ UnitId, UserId }
import hr.com.blanka.apartments.common.DayMonth

/*
 * Commands
 */
sealed trait PriceCommand {
  def userId: UserId

  def unitId: UnitId
}

case class SavePriceRange(userId: UserId,
                          unitId: UnitId,
                          from: LocalDate,
                          to: LocalDate,
                          price: BigDecimal)
    extends PriceCommand

case class SavePriceForSingleDay(userId: UserId, unitId: UnitId, day: DayMonth, price: BigDecimal)
    extends PriceCommand

/*
 * Events
 */
case class PriceRangeSaved(userId: UserId,
                           unitId: UnitId,
                           from: LocalDate,
                           to: LocalDate,
                           price: BigDecimal)

case class DailyPriceSaved(userId: UserId,
                           unitId: UnitId,
                           dayMonth: DayMonth,
                           price: BigDecimal,
                           dttm: LocalDateTime)
