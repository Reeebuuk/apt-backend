package hr.com.blanka.apartments.command.price

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.utils.HelperMethods
import hr.com.blanka.apartments.validation.BasicValidation._
import hr.com.blanka.apartments.validation.ErrorMessages._
import org.scalactic.Accumulation._
import org.scalactic.{ Bad, _ }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{ Failure, Success }

object CommandPriceActor {

  def apply() = Props(classOf[CommandPriceActor], PriceAggregateActor())
}

class CommandPriceActor(priceAggregateActorProps: Props) extends Actor with HelperMethods with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val priceAggregateActor = context.actorOf(priceAggregateActorProps, "priceAggregateActor")

  override def receive: Receive = {
    case SavePriceRange(userId, unitId, from, to, price) =>
      val msgSender = sender()

      withGood(validateDuration(from, to), validUnitId(unitId)) {
        (_, _) =>
          {
            val savedPrices = iterateThroughDays(from, to).map { localDate =>
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
