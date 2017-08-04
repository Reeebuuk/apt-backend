package hr.com.blanka.apartments.query.price

import hr.com.blanka.apartments.common.DayMonth
import java.time.LocalDate

sealed trait PriceQuery

case class LookupPriceForRange(userId: String, unitId: Int, from: LocalDate, to: LocalDate) extends PriceQuery
case class LookupAllPrices(userId: String, unitId: Int) extends PriceQuery
case class LookupPriceForDay(userId: String, unitId: Int, day: DayMonth) extends PriceQuery

sealed trait PriceQueryResponse

case class PriceForRangeCalculated(price: BigDecimal) extends PriceQueryResponse
case class PriceDayFetched(price: BigDecimal) extends PriceQueryResponse

case class DailyPrice(day: DayMonth, price: BigDecimal)
case class AllPricesFetched(pricePerDays: List[DailyPrice]) extends PriceQueryResponse

case object InvalidRange extends PriceQueryResponse
case object PriceForRangeCannotBeCalculated extends PriceQueryResponse
