package hr.com.blanka.apartments.query.price

import java.time.{ Duration, LocalDate }

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import hr.com.blanka.apartments.command.price.DailyPriceSaved
import hr.com.blanka.apartments.common.DayMonth
import hr.com.blanka.apartments.query.booking.StartSync
import hr.com.blanka.apartments.utils.PredefinedTimeout
import org.scalactic.{ Bad, Good }

import scala.collection.immutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.util.{ Failure, Success }

object QueryPriceRangeActor {
  def apply() = Props(classOf[QueryPriceRangeActor])
}

class QueryPriceRangeActor extends Actor with PredefinedTimeout {

  val dailyPriceAggregateActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "DailyPriceAggregateActor",
    entityProps = DailyPriceAggregateActor(self),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = DailyPriceAggregateActor.extractEntityId,
    extractShardId = DailyPriceAggregateActor.extractShardId
  )

  def sendMessagesForSingleDayCalculations(
      calculatePriceForRange: LookupPriceForRange
  ): immutable.IndexedSeq[Future[Any]] = {
    import calculatePriceForRange._

    (0l until Duration.between(from.atStartOfDay(), to.atStartOfDay()).toDays)
      .map(daysFromStart => {
        val day = DayMonth(LocalDate.from(from).plusDays(daysFromStart))
        dailyPriceAggregateActor ? LookupPriceForDay(userId, unitId, day)
      })

  }

  override def receive: Receive = {
    case e: DailyPriceSaved =>
      val msgSender = sender()
      dailyPriceAggregateActor ? e pipeTo msgSender
    case cpfr: LookupPriceForRange =>
      val msgSender                         = sender()
      val newlySentDailyCalculationMessages = sendMessagesForSingleDayCalculations(cpfr)

      Future.sequence(newlySentDailyCalculationMessages).onComplete {
        case Success(result) =>
          msgSender ! Good(
            result.foldLeft(BigDecimal(0))(
              (sum, next) => next.asInstanceOf[PriceDayFetched].price + sum
            )
          )
        case Failure(t) => msgSender ! Bad("An error has occurred: " + t.getMessage)
      }
    case lap: LookupAllPrices =>
      val msgSender   = sender()
      val currentYear = LocalDate.now().getYear
      val newlySentDailyCalculationMessages = sendMessagesForSingleDayCalculations(
        LookupPriceForRange(userId = lap.userId,
                            unitId = lap.unitId,
                            from = LocalDate.ofYearDay(currentYear, 1),
                            to = LocalDate.of(currentYear, 12, 31))
      )

      Future.sequence(newlySentDailyCalculationMessages).onComplete {
        case Success(result) =>
          msgSender ! Good(
            result.foldLeft(BigDecimal(0))(
              (sum, next) => next.asInstanceOf[PriceDayFetched].price + sum
            )
          )
        case Failure(t) => msgSender ! Bad("An error has occurred: " + t.getMessage)
      }
    case e: StartSync =>
      context.parent ! e

  }

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 2 seconds) {
      case _ => Restart
    }
}
