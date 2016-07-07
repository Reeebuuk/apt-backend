package hr.com.blanka.apartments.query.price

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.command.price.DayMonth
import org.joda.time.{DateTime, DateTimeZone, Days}
import org.scalactic.{Bad, Good}

import scala.collection.immutable.Map
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{Failure, Success}

object QueryPriceRangeActor {
  def apply(dailyPriceActor: ActorRef) = Props(classOf[QueryPriceRangeActor], dailyPriceActor)
}

case class CalculationData(singleDayCalculations: Map[Long, Option[BigDecimal]])

class QueryPriceRangeActor(dailyPriceActor: ActorRef) extends Actor {

  implicit val timeout = Timeout(10 seconds)

  override def receive = active(Map[Long, CalculationData]())

  def sendMessagesForSingleDayCalculations(calculatePriceForRange: LookupPriceForRange) = {
    import calculatePriceForRange._

    val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
    val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)
    val duration = Days.daysBetween(fromDate.toLocalDate, toDate.toLocalDate).getDays

    (0 until duration).map(daysFromStart => {
      val day = DayMonth(new DateTime(from).toDateTime(DateTimeZone.UTC).plusDays(daysFromStart).getMillis)
      dailyPriceActor ? LookupPriceForDay(userId, unitId, day)
    })

  }

  def active(priceRangeCalculations: Map[Long, CalculationData]): Receive = {
    case cpfr: LookupPriceForRange =>
      val msgSender = sender()
      val newlySentDailyCalculationMessages = sendMessagesForSingleDayCalculations(cpfr)

      Future.sequence(newlySentDailyCalculationMessages).onComplete {
        case Success(result) => msgSender ! Good(result.foldLeft(0.0)((sum, next) => next.asInstanceOf[PriceDayFetched].price + sum))
        case Failure(t) => msgSender ! Bad("An error has occurred: " + t.getMessage)
      }
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 2 seconds) {
      case x => Restart
    }
}