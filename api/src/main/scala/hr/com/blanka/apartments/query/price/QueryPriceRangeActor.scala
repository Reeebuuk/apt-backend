package hr.com.blanka.apartments.query.price

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.command.price.DayMonth
import org.joda.time.{ Days, LocalDate }
import org.scalactic.{ Bad, Good }

import scala.collection.immutable.Map
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{ Failure, Success }

object QueryPriceRangeActor {
  def apply(dailyPriceActor: ActorRef) = Props(classOf[QueryPriceRangeActor], dailyPriceActor)
}

case class CalculationData(singleDayCalculations: Map[Long, Option[BigDecimal]])

class QueryPriceRangeActor(dailyPriceActor: ActorRef) extends Actor {

  implicit val timeout = Timeout(10 seconds)

  override def receive = active(Map[Long, CalculationData]())

  def sendMessagesForSingleDayCalculations(calculatePriceForRange: LookupPriceForRange) = {
    import calculatePriceForRange._

    val fromDate = new LocalDate(from)
    val toDate = new LocalDate(to)

    (0 until Days.daysBetween(fromDate, toDate).getDays).map(daysFromStart => {
      val day = DayMonth(new LocalDate(from).plusDays(daysFromStart))
      dailyPriceActor ? LookupPriceForDay(userId, unitId, day)
    })

  }

  def active(priceRangeCalculations: Map[Long, CalculationData]): Receive = {
    case cpfr: LookupPriceForRange =>
      val msgSender = sender()
      val newlySentDailyCalculationMessages = sendMessagesForSingleDayCalculations(cpfr)

      Future.sequence(newlySentDailyCalculationMessages).onComplete {
        case Success(result) => msgSender ! Good(result.foldLeft(BigDecimal(0))((sum, next) => next.asInstanceOf[PriceDayFetched].price + sum))
        case Failure(t) => msgSender ! Bad("An error has occurred: " + t.getMessage)
      }
  }

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 2 seconds) {
      case x => Restart
    }
}