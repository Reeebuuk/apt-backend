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

  case class DailyPriceCalculated(requestId: Long, unitId: Int, day: Long, price: BigDecimal) extends QueryResponse
  case class DailyPriceCannotBeCalculated(requestId: Long, unitId: Int) extends QueryResponse

  def apply(pricingConfig: PricingConfig) = Props(classOf[DailyPriceActor], pricingConfig)

}

class DailyPriceActor(pricingConfig: PricingConfig) extends Actor with ActorLogging {

  import DailyPriceActor._

  override def receive: Receive = {
    case CalculatePriceForDay(requestId, unitId, day) => {
      pricingConfig.pricings
        .filter(x => x.from <= day && x.to >= day)
        .map(x => x.appPrice(unitId)) match {
        case price :: Nil => sender() ! DailyPriceCalculated(requestId, unitId, day, price)
        case _  => sender() ! DailyPriceCannotBeCalculated(requestId, unitId)
      }
    }
  }

}