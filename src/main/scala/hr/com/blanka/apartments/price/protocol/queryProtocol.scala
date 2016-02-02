package hr.com.blanka.apartments.price.protocol

import scala.concurrent.Promise

sealed trait PriceQuery

case class LookupPriceForRange(userId: String, unitId: Int, from: Long, to: Long, pricePromise: Promise[PriceQueryResponse]) extends PriceQuery
case class LookupPriceForDay(userId: String, unitId: Int, requestId: Long, from: Long) extends PriceQuery

sealed trait PriceQueryResponse

case class PriceForRangeCalculated(price: Int) extends PriceQueryResponse
case class PriceDayFetched(requestId: Long, day: Long, price: Int) extends PriceQueryResponse

case object InvalidRange extends PriceQueryResponse
case object PriceForRangeCannotBeCalculated extends PriceQueryResponse
