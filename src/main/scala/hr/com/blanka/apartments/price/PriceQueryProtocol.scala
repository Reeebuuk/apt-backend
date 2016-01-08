package hr.com.blanka.apartments.price

import scala.concurrent.Promise

object PriceQueryProtocol {

  sealed trait PriceMsg

  sealed trait PriceQuery extends PriceMsg

  case class CalculatePriceForRange(unitId: Int, from: Long, to: Long) extends PriceQuery

  sealed trait PriceQueryResponse extends PriceMsg

  case class PriceForRangeCalculated(price: BigDecimal) extends PriceQueryResponse

  case object InvalidRange extends PriceQueryResponse
  case object PriceForRangeCannotBeCalculated extends PriceQueryResponse

}
