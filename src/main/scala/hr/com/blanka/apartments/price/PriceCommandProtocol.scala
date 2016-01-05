package hr.com.blanka.apartments.price

import scala.concurrent.Promise

object PriceCommandProtocol {

  sealed trait SavePriceMsg

  sealed trait SavePriceCommand extends SavePriceMsg

  case class SavePriceForRange(unitId: Int, from: Long, to: Long, pricePromise: Promise[SavePriceCommandResponse]) extends SavePriceCommand

  sealed trait SavePriceCommandResponse extends SavePriceMsg

  case object PriceForRangeSaved extends SavePriceCommandResponse

  case object InvalidSavePriceRange extends SavePriceCommandResponse
  case object SavePriceForRangeCannotBeSaved extends SavePriceCommandResponse

}
