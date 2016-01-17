package hr.com.blanka.apartments.price

import scala.concurrent.Promise

object PriceQueryProtocol {

  sealed trait PriceMsg

  sealed trait PriceQuery extends PriceMsg

  case class LookupPriceForRange(unitId: Int, from: Long, to: Long, pricePromise: Promise[PriceQueryResponse]) extends PriceQuery

  sealed trait PriceQueryResponse extends PriceMsg

  case class PriceForRangeCalculated(price: Int) extends PriceQueryResponse

  case object InvalidRange extends PriceQueryResponse
  case object PriceForRangeCannotBeCalculated extends PriceQueryResponse

}
