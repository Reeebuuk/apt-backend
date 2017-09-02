package hr.com.blanka.apartments.query.price

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.ask
import hr.com.blanka.apartments.command.price.PriceAggregateActor
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
                                                   DayMonth(date),
                                                   lookupPriceForRange.validOn)
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

  context.parent ! StartSync(dailyPriceAggregateActor, PriceAggregateActor.persistenceId, 0)

  override def receive: Receive = {
    case lookupPriceForRange: LookupPriceForRange =>
      val msgSender = sender()
      val newlySentDailyCalculationMessages = sendMessagesForSingleDayCalculations(
        dailyPriceAggregateActor = dailyPriceAggregateActor,
        lookupPriceForRange = lookupPriceForRange
      )

      Future.sequence(newlySentDailyCalculationMessages).map { result =>
        msgSender ! Good(
          PriceForRangeCalculated(
            enquiryId = lookupPriceForRange.enquiryId,
            price = result.foldLeft(BigDecimal(0))(
              (sum, next) => next.asInstanceOf[PriceDayFetched].price + sum
            )
          )
        )
      }
  }

  override val supervisorStrategy: OneForOneStrategy =
    OneForOneStrategy(maxNrOfRetries = 2, withinTimeRange = 2 seconds) {
      case _ => Restart
    }
}
