package hr.com.blanka.apartments.price

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.price.protocol.{SavePriceRange, SavePriceForSingleDay}
import hr.com.blanka.apartments.validation.BasicValidation._
import org.joda.time.{DateTime, DateTimeZone}
import org.scalactic.Accumulation._
import org.scalactic._

import scala.collection.immutable.IndexedSeq
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object CommandPriceRangeActor {

  def apply(dailyPriceActor: ActorRef) = Props(classOf[CommandPriceRangeActor], dailyPriceActor)
}

class CommandPriceRangeActor(dailyPriceActor: ActorRef) extends Actor with ActorLogging {

  implicit val timeout = Timeout(10 seconds)

  override def receive: Receive = {
    case SavePriceRange(userId, unitId, from, to, price) => {
      val msgSender = sender()
      val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
      val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)

      val validationResult = withGood(validDuration(fromDate, toDate), validUnitId(unitId)) {
        (duration, unitId) => {
          val newDailyPrices: IndexedSeq[Future[Any]] = (0 until duration).map(daysFromStart => {
            val day = fromDate.plusDays(daysFromStart).getMillis

            dailyPriceActor ? SavePriceForSingleDay(userId, unitId, day, price)
          })

          Future.sequence(newDailyPrices).onComplete {
            case Success(result) => msgSender ! result.head
            case Failure(t) => msgSender ! Bad("An error has occurred: " + t.getMessage)
          }
        }
      }

      validationResult.badMap(msgSender ! Bad(_))
    }
  }

}
