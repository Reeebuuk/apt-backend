package hr.com.blanka.apartments.price

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.http.routes.SavePriceForRange
import hr.com.blanka.apartments.price.protocol.SavePriceForSingleDay
import org.joda.time.{DateTime, DateTimeZone, Days}
import org.scalactic.Good

import scala.collection.immutable.IndexedSeq
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

object CommandPriceRangeActor {

  def apply() = Props(classOf[CommandPriceRangeActor])
}

class CommandPriceRangeActor extends Actor with ActorLogging {

  implicit val timeout = Timeout(2 seconds)

  import context.dispatcher

  val dailyPriceActor = context.actorOf(Props(classOf[DailyPriceAggregateActor]))

  override def receive: Receive = {
    case SavePriceForRange(userId, unitId, from, to, price) => {
      val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
      val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)
      val duration = Days.daysBetween(fromDate.toLocalDate, toDate.toLocalDate).getDays

      val newDailyPrices: IndexedSeq[Future[Any]] = (0 until duration).map(daysFromStart => {
        val day = new DateTime(from).toDateTime(DateTimeZone.UTC).plusDays(daysFromStart).getMillis

        dailyPriceActor ? SavePriceForSingleDay(userId, unitId, day, price)
      })

      Future.sequence(newDailyPrices).onSuccess({ case _ => sender() ! Good })
    }
  }

}
