package com.example.crudapi.price

import akka.actor.{ActorLogging, Props, Terminated}
import akka.persistence.PersistentActor
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import akka.util.Timeout
import com.example.crudapi.price.DailyPriceActor.{CalculatePriceForDay, DailyPriceCalculated}
import com.example.crudapi.price.PriceCommandQueryProtocol.{CalculatePriceForRange, PriceForRangeCalculated}
import com.example.crudapi.utils.PricingConfig
import org.joda.time.{DateTime, DateTimeZone, Days}

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object PriceRangeActor {

  def props(pricingConfig: PricingConfig) = Props(new PriceRangeActor(pricingConfig))

}

case class PriceForDay(day: Long, var price: Option[BigDecimal] = None)

class PriceRangeActor(pricingConfig: PricingConfig) extends PersistentActor with ActorLogging {

  def persistenceId = "PriceRangeActor"

  implicit val timeout = Timeout(5 seconds)

  val dailyPriceActor = context.actorOf(DailyPriceActor.props(pricingConfig), "DailyPriceActor")

  var requestId: Long = 0

  val priceRangeCalculationsForRequests = mutable.Map[Long, mutable.Map[Long, Option[BigDecimal]]]()

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props(classOf[DailyPriceActor], pricingConfig))
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  override def receiveCommand: Receive = {
    case CalculatePriceForRange(unitId, from, to) => {
      val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
      val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)
      val duration = Days.daysBetween(fromDate.toLocalDate, toDate.toLocalDate).getDays
      requestId += 1

      val priceRangeCalculations = mutable.Map[Long, Map[Long, Option[BigDecimal]]]()

      (0 until duration).foreach(daysFromStart => {
        val day = new DateTime(from).toDateTime(DateTimeZone.UTC).plusDays(daysFromStart).getMillis
        priceRangeCalculations + (day -> None)
        router.route(CalculatePriceForDay(requestId, unitId, day), sender())
      })

      priceRangeCalculationsForRequests + (requestId -> priceRangeCalculations)
    }

    case DailyPriceCalculated(id, unitId, day, price) => {
      val previousCalculations = priceRangeCalculationsForRequests.getOrElse(id, sys.error(s"no map for requestId: $id"))
      previousCalculations + (day -> Option(price))
      priceRangeCalculationsForRequests + (id -> previousCalculations)

      if (previousCalculations.values.forall(_.isDefined)) {
        PriceForRangeCalculated(unitId,
          previousCalculations.values.foldLeft(BigDecimal(0))((sum, value) => sum + value.get))
      }
    }

    case Terminated(a) => {
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[DailyPriceActor])
      context watch r
      router = router.addRoutee(r)
    }

  }

  override def receiveRecover: Receive = {
    case _ =>
  }
}