package com.example.crudapi.price

import akka.actor.{Actor, ActorLogging, Props}
import com.example.crudapi.utils.PricingConfig

object DailyPriceActor {

  sealed trait DailyPriceMsg

  sealed trait Query extends DailyPriceMsg

  case class CalculatePriceForDay(id: Long, unitId: Int, day: Long) extends Query

  sealed trait QueryResponse extends DailyPriceMsg {
    def requestId: Long
  }

  case class DailyPriceCalculated(requestId: Long, day: Long, price: BigDecimal) extends QueryResponse
  case class DailyPriceCannotBeCalculated(requestId: Long) extends QueryResponse

  def apply(pricingConfig: PricingConfig) = Props(classOf[DailyPriceActor], pricingConfig)

}

class DailyPriceActor(pricingConfig: PricingConfig) extends Actor with ActorLogging {

  import DailyPriceActor._

  override def receive: Receive = {
    case CalculatePriceForDay(requestId, unitId, day) => {
      pricingConfig.pricings
        .filter(x => x.from <= day && x.to >= day)
        .map(x => x.appPrice(unitId)) match {
        case price :: Nil => sender() ! DailyPriceCalculated(requestId, day, price)
        case _  => sender() ! DailyPriceCannotBeCalculated(requestId)
      }
    }
  }

}