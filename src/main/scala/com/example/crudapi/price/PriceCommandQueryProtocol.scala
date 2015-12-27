package com.example.crudapi.price

import scala.concurrent.Promise

object PriceCommandQueryProtocol {

  sealed trait PriceMsg {
    val unitId: Int
  }

  sealed trait PriceQuery extends PriceMsg

  case class CalculatePriceForRange(unitId: Int, from: Long, to: Long, pricePromise: Promise[PriceQueryResponse]) extends PriceQuery

  sealed trait PriceQueryResponse extends PriceMsg

  case class PriceForRangeCalculated(unitId: Int, price: BigDecimal) extends PriceQueryResponse

  case class InvalidRange(unitId: Int) extends PriceQueryResponse

}
