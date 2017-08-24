package hr.com.blanka.apartments.query.price

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.pattern.{ ask, pipe }
import hr.com.blanka.apartments.command.price.DailyPriceSaved
import hr.com.blanka.apartments.query.booking.StartSync
import hr.com.blanka.apartments.utils.PredefinedTimeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object QueryPriceActor {
  def apply() = Props(classOf[QueryPriceActor])
}

class QueryPriceActor extends Actor with ActorLogging with PredefinedTimeout {

  val queryPriceRangeActor: ActorRef =
    context.actorOf(QueryPriceRangeActor(), "QueryPriceRangeActor")

  val pricingLegacyActor: ActorRef =
    context.actorOf(PricingLegacyActor(), "PricingLegacyActor")

  override def receive: Receive = {
    case e @ (_: LookupPriceForRange | _: DailyPriceSaved) =>
      val msgSender = sender()
      queryPriceRangeActor ? e pipeTo msgSender
    case e: LegacyLookupAllPrices =>
      val msgSender = sender()
      pricingLegacyActor ? e pipeTo msgSender
    case e: StartSync =>
      context.parent ! e
  }
}
