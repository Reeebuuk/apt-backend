package hr.com.blanka.apartments.price

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.util.Timeout
import hr.com.blanka.apartments.price.DailyPriceActor.{CalculatePriceForDay, DailyPriceCalculated, DailyPriceCannotBeCalculated}
import hr.com.blanka.apartments.price.PriceQueryProtocol.{CalculatePriceForRange, PriceForRangeCalculated, PriceForRangeCannotBeCalculated, PriceQueryResponse}
import hr.com.blanka.apartments.utils.PricingConfig
import org.joda.time.{DateTime, DateTimeZone, Days}

import scala.collection.immutable.Map
import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object PriceRangeActor {

  def apply(pricingConfig: PricingConfig) = Props(classOf[PriceRangeActor], pricingConfig)

}

case class CalculationData(singleDayCalculations: Map[Long, Option[BigDecimal]], resultPromise: Promise[PriceQueryResponse])

class PriceRangeActor(pricingConfig: PricingConfig) extends Actor {

  implicit val timeout = Timeout(5 seconds)

  val dailyPriceActor = context.actorOf(DailyPriceActor(pricingConfig), "daily-price-calculators")

  override def receive = active(0, Map[Long, CalculationData]())

  def sendMessagesForSingleDayCalculations(requestId: Long, calculatePriceForRange: CalculatePriceForRange) = {
    import calculatePriceForRange._

    val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
    val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)
    val duration = Days.daysBetween(fromDate.toLocalDate, toDate.toLocalDate).getDays

    val singleDayCalculations = (0 until duration).map(daysFromStart => {
      val day = new DateTime(from).toDateTime(DateTimeZone.UTC).plusDays(daysFromStart).getMillis
      dailyPriceActor ! CalculatePriceForDay(requestId, unitId, day)
      day -> None
    }) toMap

    requestId -> CalculationData(singleDayCalculations, pricePromise)
  }

  def active(lastRequestId: Long, priceRangeCalculations: Map[Long, CalculationData]): Receive = {
    case cpfr: CalculatePriceForRange => {

      val newRequestId = lastRequestId + 1
      val newlySentDailyCalculationMessages = sendMessagesForSingleDayCalculations(newRequestId, cpfr)

      context become active(newRequestId, priceRangeCalculations + newlySentDailyCalculationMessages)
    }

    case DailyPriceCalculated(reqId, day, price) => {

      val currentCalculationData = priceRangeCalculations.getOrElse(reqId, sys.error(s"No CalculationData for reqId: $reqId"))

      val newCalculationAdded = currentCalculationData.singleDayCalculations + (day -> Option(price))
      val isPriceCalculatedForWholeRange = newCalculationAdded.values.forall(_.isDefined)

      if (isPriceCalculatedForWholeRange) {
        currentCalculationData.resultPromise.success(
          PriceForRangeCalculated(newCalculationAdded.values.foldLeft(BigDecimal(0))((sum, value) => sum + value.get))
        )
        context become active(lastRequestId, priceRangeCalculations - reqId)
      }
      else {
        context become active(lastRequestId, priceRangeCalculations + (reqId -> CalculationData(newCalculationAdded, currentCalculationData.resultPromise)))
      }
    }

    case DailyPriceCannotBeCalculated(reqId) => {
      priceRangeCalculations.get(reqId) match {
        case Some(calculationData) =>
          calculationData.resultPromise.success(PriceForRangeCannotBeCalculated)
          context become active(lastRequestId, priceRangeCalculations - reqId)
        case None =>
      }
    }

  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 2 seconds) {
      case x => Restart
    }
}