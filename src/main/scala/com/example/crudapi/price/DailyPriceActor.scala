package com.example.crudapi.price

import akka.actor.{Actor, ActorLogging, Props}
import com.example.crudapi.price.PriceProtocol.DailyPriceCalculated
import com.example.crudapi.utils.PricingConfig

object DailyPriceActor {

  sealed trait Command

  case class CalculatePriceForDay(unitId: Int, day: Long) extends Command

  sealed trait Query

  case object GetSalesRecords extends Query

  def props(pricingConfig: PricingConfig) = Props(new DailyPriceActor(pricingConfig))

}

class DailyPriceActor(pricingConfig: PricingConfig) extends Actor with ActorLogging {

  import DailyPriceActor._

  override def receive: Receive = {
    case CalculatePriceForDay(unitId, day) => {
      val price = pricingConfig.pricings.filter(x => x.from <= day && x.to >= day)
        .map(x => x.appPrice(unitId)).head
      sender() ! DailyPriceCalculated(unitId, day, price)
    }
  }

}