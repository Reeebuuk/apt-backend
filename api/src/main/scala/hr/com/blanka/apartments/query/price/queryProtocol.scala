package hr.com.blanka.apartments.query.price

import hr.com.blanka.apartments.common.DayMonth
import java.time.LocalDate

import hr.com.blanka.apartments.common.ValueClasses.{ UnitId, UserId }

sealed trait PriceQuery

case class LookupPriceForRange(userId: UserId, unitId: UnitId, from: LocalDate, to: LocalDate)
    extends PriceQuery
case class LegacyLookupAllPrices(userId: UserId)                            extends PriceQuery
case class LookupPriceForDay(userId: UserId, unitId: UnitId, day: DayMonth) extends PriceQuery

sealed trait PriceQueryResponse

case class PriceForRangeCalculated(price: BigDecimal) extends PriceQueryResponse
case class PriceDayFetched(price: BigDecimal)         extends PriceQueryResponse

case class DailyPrice(day: DayMonth, price: BigDecimal)
case class AllPricesFetched(pricePerDays: List[DailyPrice]) extends PriceQueryResponse

case object InvalidRange                    extends PriceQueryResponse
case object PriceForRangeCannotBeCalculated extends PriceQueryResponse

case class PricePerPeriod(from: DayMonth, to: DayMonth, appPrice: Map[UnitId, Int])
