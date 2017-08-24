package hr.com.blanka.apartments.query.price

import java.time.LocalDate

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import hr.com.blanka.apartments.command.price.DailyPriceSaved
import hr.com.blanka.apartments.common.DayMonth
import hr.com.blanka.apartments.query.booking.StartSync
import hr.com.blanka.apartments.utils.PredefinedTimeout
import org.scalactic.Good

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object QueryPriceRangeActor extends PredefinedTimeout {
  def apply() = Props(classOf[QueryPriceRangeActor])

  import hr.com.blanka.apartments.utils.DateHelperMethods._

  def sendMessagesForSingleDayCalculations(
      dailyPriceAggregateActor: ActorRef,
      lookupPriceForRange: LookupPriceForRange
  ): List[Future[Any]] =
    iterateThroughDaysExcludingLast(lookupPriceForRange.from, lookupPriceForRange.to).map { date =>
      dailyPriceAggregateActor ? LookupPriceForDay(lookupPriceForRange.userId,
                                                   lookupPriceForRange.unitId,
                                                   DayMonth(date))
    }
}

class QueryPriceRangeActor extends Actor with PredefinedTimeout {

  import QueryPriceRangeActor._

  val dailyPriceAggregateActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "DailyPriceAggregateActor",
    entityProps = DailyPriceAggregateActor(self),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = DailyPriceAggregateActor.extractEntityId,
    extractShardId = DailyPriceAggregateActor.extractShardId
  )

  override def receive: Receive = {
    case e: DailyPriceSaved =>
      val msgSender = sender()
      dailyPriceAggregateActor ? e pipeTo msgSender
    case cpfr: LookupPriceForRange =>
      val msgSender = sender()
      val newlySentDailyCalculationMessages = sendMessagesForSingleDayCalculations(
        dailyPriceAggregateActor = dailyPriceAggregateActor,
        lookupPriceForRange = cpfr
      )

      Future.sequence(newlySentDailyCalculationMessages).map { result =>
        msgSender ! Good(
          result.foldLeft(BigDecimal(0))(
            (sum, next) => next.asInstanceOf[PriceDayFetched].price + sum
          )
        )
      }
    case lap: LookupAllPrices =>
      val msgSender   = sender()
      val currentYear = LocalDate.now().getYear
      val newlySentDailyCalculationMessages = sendMessagesForSingleDayCalculations(
        dailyPriceAggregateActor = dailyPriceAggregateActor,
        lookupPriceForRange = LookupPriceForRange(userId = lap.userId,
                                                  unitId = lap.unitId,
                                                  from = LocalDate.ofYearDay(currentYear, 1),
                                                  to = LocalDate.of(currentYear, 12, 31))
      )

      Future.sequence(newlySentDailyCalculationMessages).map { result =>
        msgSender ! Good(
          result.foldLeft(BigDecimal(0))(
            (sum, next) => next.asInstanceOf[PriceDayFetched].price + sum
          )
        )
      }
    case e: StartSync =>
      context.parent ! e

  }

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 2 seconds) {
      case _ => Restart
    }
}
