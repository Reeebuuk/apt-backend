package com.example.crudapi.price

import akka.actor.{Actor, Props, ActorLogging, Terminated}
import akka.persistence.PersistentActor
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.util.Timeout
import com.example.crudapi.price.DailyPriceActor.{CalculatePriceForDay, DailyPriceCalculated}
import com.example.crudapi.price.PriceCommandQueryProtocol.{CalculatePriceForRange, PriceForRangeCalculated, PriceQueryResponse}
import com.example.crudapi.utils.PricingConfig
import org.joda.time.{DateTime, DateTimeZone, Days}

import scala.collection.mutable
import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object PriceRangeActor {

  def apply(pricingConfig: PricingConfig) = Props(classOf[PriceRangeActor], pricingConfig)

}

case class PriceForDay(day: Long, var price: Option[BigDecimal] = None)

class PriceRangeActor(pricingConfig: PricingConfig) extends Actor {

  implicit val timeout = Timeout(5 seconds)

  val dailyPriceActor = context.actorOf(DailyPriceActor(pricingConfig), "DailyPriceActor")

  var requestId: Long = 0

  val priceRangeCalculationsForRequests = mutable.Map[Long, mutable.Map[Long, Option[BigDecimal]]]()
  val pricePromises = mutable.Map[Long, Promise[PriceQueryResponse]]()

//  var router = {
//    val routees = Vector.fill(5) {
//      val r = context.actorOf(DailyPriceActor(pricingConfig))
//      context watch r
//      ActorRefRoutee(r)
//    }
//    Router(RoundRobinRoutingLogic(), routees)
//  }

  override def receive: Receive = {
    case CalculatePriceForRange(unitId, from, to, pricePromise) => {
      val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
      val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)
      val duration = Days.daysBetween(fromDate.toLocalDate, toDate.toLocalDate).getDays
      requestId += 1

      val priceRangeCalculations = mutable.Map[Long, Map[Long, Option[BigDecimal]]]()

      pricePromises += (requestId -> pricePromise)

      (0 until duration).foreach(daysFromStart => {
        val day = new DateTime(from).toDateTime(DateTimeZone.UTC).plusDays(daysFromStart).getMillis
        priceRangeCalculations + (day -> None)
        priceRangeCalculationsForRequests += (requestId -> priceRangeCalculations)
        dailyPriceActor ! CalculatePriceForDay(requestId, unitId, day)
      })
    }

    case DailyPriceCalculated(id, unitId, day, price) => {
      val previousCalculations = priceRangeCalculationsForRequests.getOrElse(id, sys.error(s"no map for requestId: $id"))
      previousCalculations + (day -> Option(price))
      priceRangeCalculationsForRequests += (id -> previousCalculations)

      if (previousCalculations.values.forall(_.isDefined)) {
        pricePromises.get(id).get.success(
        PriceForRangeCalculated(unitId,
          previousCalculations.values.foldLeft(BigDecimal(0))((sum, value) => sum + value.get)))
      }
    }

    case Terminated(a) => {
//      router = router.removeRoutee(a)
//      val r = context.actorOf(DailyPriceActor(pricingConfig))
//      context watch r
//      router = router.addRoutee(r)
    }

  }
}