package com.example.crudapi.price

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import akka.util.Timeout
import com.example.crudapi.price.DailyPriceActor.{CalculatePriceForDay, DailyPriceCalculated, DailyPriceCannotBeCalculated}
import com.example.crudapi.price.PriceCommandQueryProtocol.{CalculatePriceForRange, PriceForRangeCalculated, PriceForRangeCannotBeCalculated, PriceQueryResponse}
import com.example.crudapi.utils.PricingConfig
import org.joda.time.{DateTime, DateTimeZone, Days}

import scala.collection.immutable.Map
import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object PriceRangeActor {

  def apply(pricingConfig: PricingConfig) = Props(classOf[PriceRangeActor], pricingConfig)

}

class PriceRangeActor(pricingConfig: PricingConfig) extends Actor {

  implicit val timeout = Timeout(5 seconds)

  val dailyPriceActor = context.actorOf(DailyPriceActor(pricingConfig), "daily-price-calculators")

  var requestId: Long = 0

  override def receive = active(Map[Long, (Map[Long, Option[BigDecimal]], Promise[PriceQueryResponse])]())

  def active(priceRangeCalculations: Map[Long, (Map[Long, Option[BigDecimal]], Promise[PriceQueryResponse])]): Receive = {
    case CalculatePriceForRange(unitId, from, to, pricePromise) => {
      val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
      val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)
      val duration = Days.daysBetween(fromDate.toLocalDate, toDate.toLocalDate).getDays
      requestId += 1

      val pricesForIndividualDays: Map[Long, Option[BigDecimal]] = (0 until duration).map(daysFromStart => {
        val day = new DateTime(from).toDateTime(DateTimeZone.UTC).plusDays(daysFromStart).getMillis
        dailyPriceActor ! CalculatePriceForDay(requestId, unitId, day)
        day -> None
      }) toMap

      context become active(priceRangeCalculations + (requestId ->(pricesForIndividualDays, pricePromise)))
    }

    case DailyPriceCalculated(reqId, unitId, day, price) => {
      val previousCalculations = priceRangeCalculations.getOrElse(reqId, sys.error(s"no map for reqId: $reqId"))
      val newCalculationAdded = previousCalculations._1 + (day -> Option(price))

      if (newCalculationAdded.values.forall(_.isDefined)) {
        previousCalculations._2.success(
          PriceForRangeCalculated(unitId,
            newCalculationAdded.values.foldLeft(BigDecimal(0))((sum, value) => sum + value.get)))
        context become active(priceRangeCalculations - reqId)
      }
      else {
        context become active(priceRangeCalculations + (reqId ->(newCalculationAdded, previousCalculations._2)))
      }
    }

    case DailyPriceCannotBeCalculated(reqId, unitId) => {
      priceRangeCalculations.get(reqId) match {
        case Some(value) =>
          value._2.success(PriceForRangeCannotBeCalculated(unitId))
          context become active(priceRangeCalculations - reqId)
        case None =>
      }
    }

  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 2 seconds) {
      case x => Stop
    }
}