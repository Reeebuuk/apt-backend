package hr.com.blanka.apartments.command.price

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.validation.BasicValidation._
import org.joda.time.{DateTime, DateTimeZone}
import org.scalactic.Accumulation._
import org.scalactic.{Bad, _}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object CommandPriceActor {

  def apply() = Props(classOf[CommandPriceActor])
}

class CommandPriceActor extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val priceAggregateActor = context.actorOf(PriceAggregateActor(), "PriceAggregateActor")

  override def receive: Receive = {
    case SavePriceRange(userId, unitId, from, to, price) =>
      val msgSender = sender()
      val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
      val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)

      withGood(validDuration(fromDate, toDate), validUnitId(unitId)) {
        (duration, unitId) => {
          val savedPrices = (0 until duration).map(daysFromStart => {
            val day = DayMonth(fromDate.plusDays(daysFromStart).getMillis)

            priceAggregateActor ? SavePriceForSingleDay(userId, unitId, day, price)
          })

          Future.sequence(savedPrices).onComplete {
            case Success(result) => msgSender ! Good
            case Failure(t) => msgSender ! Bad("An error has occurred: " + t.getMessage)
          }
        }
      } recover (err => msgSender ! Bad(err.mkString(", ")))
  }
}
