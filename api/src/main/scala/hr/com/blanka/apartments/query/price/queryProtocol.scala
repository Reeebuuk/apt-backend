package hr.com.blanka.apartments.query.price

import hr.com.blanka.apartments.common.DayMonth
import java.time.{ LocalDate, LocalDateTime }

import hr.com.blanka.apartments.common.ValueClasses.{ EnquiryId, UnitId, UserId }

sealed trait PriceQuery

case class LookupPriceForRange(enquiryId: Option[EnquiryId],
                               userId: UserId,
                               unitId: UnitId,
                               from: LocalDate,
                               to: LocalDate,
                               validOn: LocalDateTime)
    extends PriceQuery
case class LegacyLookupAllPrices(userId: UserId) extends PriceQuery
case class LookupPriceForDay(userId: UserId, unitId: UnitId, day: DayMonth, validOn: LocalDateTime)
    extends PriceQuery

sealed trait PriceQueryResponse

case class PriceForRangeCalculated(enquiryId: Option[EnquiryId], price: BigDecimal)
    extends PriceQueryResponse
case class PriceDayFetched(price: BigDecimal) extends PriceQueryResponse

case class DailyPrice(day: DayMonth, price: BigDecimal)
case class AllPricesFetched(pricePerDays: List[DailyPrice]) extends PriceQueryResponse

case object InvalidRange                    extends PriceQueryResponse
case object PriceForRangeCannotBeCalculated extends PriceQueryResponse

case class PricePerPeriod(from: DayMonth, to: DayMonth, appPrice: Map[UnitId, Int])
