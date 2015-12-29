package com.example.crudapi.price

import akka.actor.{Actor, ActorLogging, Props}
import com.example.crudapi.utils.PricingConfig

object DailyPriceActor {

  sealed trait DailyPriceMsg {
    val unitId: Int
  }

  sealed trait Query extends DailyPriceMsg

  case class CalculatePriceForDay(id: Long, unitId: Int, day: Long) extends Query

  sealed trait QueryResponse extends DailyPriceMsg

  case class DailyPriceCalculated(id: Long, unitId: Int, day: Long, price: BigDecimal) extends QueryResponse
  case class DailyPriceCannotBeCalculated(id: Long, unitId: Int) extends QueryResponse

  def apply(pricingConfig: PricingConfig) = Props(classOf[DailyPriceActor], pricingConfig)

}

class DailyPriceActor(pricingConfig: PricingConfig) extends Actor with ActorLogging {

  import DailyPriceActor._

  override def receive: Receive = {
    case CalculatePriceForDay(id, unitId, day) => {
      pricingConfig.pricings
        .filter(x => x.from <= day && x.to >= day)
        .map(x => x.appPrice(unitId)) match {
        case price :: Nil => sender() ! DailyPriceCalculated(id, unitId, day, price)
        case _  => sender() ! DailyPriceCannotBeCalculated(id, unitId)
      }
    }
  }

}