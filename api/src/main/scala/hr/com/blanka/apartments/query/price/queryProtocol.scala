package hr.com.blanka.apartments.query.price

import hr.com.blanka.apartments.command.price.DayMonth
import org.joda.time.LocalDate

sealed trait PriceQuery

case class LookupPriceForRange(userId: String, unitId: Int, from: LocalDate, to: LocalDate) extends PriceQuery
case class LookupPriceForDay(userId: String, unitId: Int, day: DayMonth) extends PriceQuery

sealed trait PriceQueryResponse

case class PriceForRangeCalculated(price: BigDecimal) extends PriceQueryResponse
case class PriceDayFetched(price: BigDecimal) extends PriceQueryResponse

case object InvalidRange extends PriceQueryResponse
case object PriceForRangeCannotBeCalculated extends PriceQueryResponse
