package hr.com.blanka.apartments.price.protocol

/*
* Commands
*/

sealed trait Command {
  def unitId: Int
  def userId: String
}

case class SavePriceRange(userId: String, unitId: Int, from: Long, to: Long, price: Int) extends Command
case class SavePriceForSingleDay(userId: String, unitId: Int, day: Long, price: Int) extends Command

/*
* Events
*/

case class PriceRangeSaved(unitId: Int, from: Long, to: Long, price: Int)