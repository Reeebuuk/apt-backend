package hr.com.blanka.apartments.command.price

import akka.actor._
import akka.pattern.ask
import hr.com.blanka.apartments.common.DayMonth
import hr.com.blanka.apartments.utils.{ DateHelperMethods, PredefinedTimeout }
import hr.com.blanka.apartments.validation.BasicValidation._
import hr.com.blanka.apartments.validation.ErrorMessages._
import org.scalactic.Accumulation._
import org.scalactic._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{ Failure, Success }

object CommandPriceActor {

  def apply() = Props(classOf[CommandPriceActor], PriceAggregateActor())
}

class CommandPriceActor(priceAggregateActorProps: Props)
    extends Actor
    with DateHelperMethods
    with ActorLogging
    with PredefinedTimeout {

  val priceAggregateActor: ActorRef =
    context.actorOf(priceAggregateActorProps, "priceAggregateActor")

  override def receive: Receive = {
    case SavePriceRange(userId, unitId, from, to, price) =>
      val msgSender = sender()

      withGood(getDuration(from, to), validUnitId(unitId)) { (_, _) =>
        {
          val savedPrices = iterateThroughDaysIncludingLast(from, to).map { localDate =>
            val day = DayMonth(localDate)

            priceAggregateActor ? SavePriceForSingleDay(userId, unitId, day, price)
          }

          Future.sequence(savedPrices).onComplete {
            case Success(result) =>
              msgSender ! Good
            case Failure(t) =>
              log.error(t.getMessage)
              msgSender ! Bad(persistingDailyPricesErrorMessage)
          }
        }
      } recover (err => msgSender ! Bad(err.mkString(", ")))
  }
}
