package hr.com.blanka.apartments.price

import org.scalactic.{Or, One}

object PriceCommandProtocol {

  case class SavePriceForRange(unitId: Int, from: Long, to: Long)

  case class SavePriceCommandResponse(response: PriceForRangeSuccess Or One[PriceForRangeError])

  sealed trait PriceForRangeSuccess

  case object PriceForRangeSaved extends PriceForRangeSuccess

  sealed trait PriceForRangeError

  case object InvalidSavePriceRange extends PriceForRangeError
  case object SavePriceForRangeCannotBeSaved extends PriceForRangeError

}
