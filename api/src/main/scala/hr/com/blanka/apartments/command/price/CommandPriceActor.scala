package hr.com.blanka.apartments.command.price

import akka.actor._
import hr.com.blanka.apartments.query.price.DailyPriceAggregateActor
import hr.com.blanka.apartments.validation.BasicValidation._
import org.joda.time.{DateTime, DateTimeZone}
import org.scalactic.Accumulation._
import org.scalactic.{Bad, _}

import scala.language.postfixOps

object CommandPriceActor {

  def apply() = Props(classOf[CommandPriceActor])
}

class CommandPriceActor extends Actor with ActorLogging {

  val dailyPriceActor = context.actorOf(PriceAggregateActor(), "PriceAggregateActor")

  override def receive: Receive = {
    case SavePriceRange(userId, unitId, from, to, price) =>
      val msgSender = sender()
      val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
      val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)

      withGood(validDuration(fromDate, toDate), validUnitId(unitId)) {
        (duration, unitId) => {
          (0 until duration).foreach(daysFromStart => {
            val day = DayMonth(fromDate.plusDays(daysFromStart).getMillis)

            dailyPriceActor ! SavePriceForSingleDay(userId, unitId, day, price)
          })
          msgSender ! Good
        }
      } recover (err => msgSender ! Bad(err.mkString(", ")))
  }
}
