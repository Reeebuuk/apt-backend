package hr.com.blanka.apartments.price

import akka.actor._
import akka.util.Timeout
import hr.com.blanka.apartments.price.protocol.{SavePriceForSingleDay, SavePriceRange}
import hr.com.blanka.apartments.validation.BasicValidation._
import org.joda.time.{DateTime, DateTimeZone}
import org.scalactic.Accumulation._
import org.scalactic.{Bad, _}

import scala.concurrent.duration._
import scala.language.postfixOps

object CommandPriceRangeActor {

  def apply(dailyPriceActor: ActorRef) = Props(classOf[CommandPriceRangeActor], dailyPriceActor)
}

class CommandPriceRangeActor(dailyPriceActor: ActorRef) extends Actor with ActorLogging {

  implicit val timeout = Timeout(10 seconds)

  override def receive: Receive = {
    case SavePriceRange(userId, unitId, from, to, price) =>
      val msgSender = sender()
      val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
      val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)

      withGood(validDuration(fromDate, toDate), validUnitId(unitId)) {
        (duration, unitId) => {
          (0 until duration).foreach(daysFromStart => {
            val day = fromDate.plusDays(daysFromStart).getMillis

            dailyPriceActor ! SavePriceForSingleDay(userId, unitId, day, price)
          })
          msgSender ! Good
        }
      } recover (err => msgSender ! Bad(err.mkString(", ")))
  }
}
