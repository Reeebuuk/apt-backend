package hr.com.blanka.apartments.price

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.price.protocol._
import org.joda.time.{DateTime, DateTimeZone, Days}
import org.scalactic.Good

import scala.collection.immutable.Map
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps

object QueryPriceRangeActor {

  def apply() = Props(classOf[QueryPriceRangeActor])

}

case class CalculationData(singleDayCalculations: Map[Long, Option[Int]], resultPromise: Promise[PriceQueryResponse])

class QueryPriceRangeActor extends Actor {

  implicit val timeout = Timeout(2 seconds)

  import context.dispatcher

  val dailyPriceActor = context.actorOf(Props(classOf[DailyPriceAggregateActor]))

  override def receive = active(Map[Long, CalculationData]())

  def sendMessagesForSingleDayCalculations(calculatePriceForRange: LookupPriceForRange) = {
    import calculatePriceForRange._

    val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
    val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)
    val duration = Days.daysBetween(fromDate.toLocalDate, toDate.toLocalDate).getDays

    (0 until duration).map(daysFromStart => {
      val day = new DateTime(from).toDateTime(DateTimeZone.UTC).plusDays(daysFromStart).getMillis
      dailyPriceActor ? LookupPriceForDay(userId, unitId, day)
    })

  }

  def active(priceRangeCalculations: Map[Long, CalculationData]): Receive = {
    case cpfr: LookupPriceForRange => {

      val newlySentDailyCalculationMessages = sendMessagesForSingleDayCalculations(cpfr)

      Future.sequence(newlySentDailyCalculationMessages).onSuccess({ case _ => sender() ! Good })

    }
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 2 seconds) {
      case x => Restart
    }
}