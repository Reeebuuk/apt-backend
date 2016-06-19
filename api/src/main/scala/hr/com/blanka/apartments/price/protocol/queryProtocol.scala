package hr.com.blanka.apartments.price.protocol

sealed trait PriceQuery

case class LookupPriceForRange(userId: String, unitId: Int, from: Long, to: Long) extends PriceQuery
case class LookupPriceForDay(userId: String, unitId: Int, day: Long) extends PriceQuery

sealed trait PriceQueryResponse

case class PriceForRangeCalculated(price: Int) extends PriceQueryResponse
case class PriceDayFetched(price: Int) extends PriceQueryResponse

case object InvalidRange extends PriceQueryResponse
case object PriceForRangeCannotBeCalculated extends PriceQueryResponse
