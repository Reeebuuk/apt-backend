package hr.com.blanka.apartments.price

import akka.actor._
import akka.cluster.sharding.ClusterSharding
import hr.com.blanka.apartments.http.routes.SavePriceForRange
import hr.com.blanka.apartments.price.protocol.SavePriceForSingleDay
import org.joda.time.{DateTime, DateTimeZone, Days}

object CommandPriceRangeActor {

  def apply() = Props(classOf[CommandPriceRangeActor])
}

class CommandPriceRangeActor extends Actor with ActorLogging {

  var as = Map.empty[Int, ActorRef]

  lazy val postRegion = ClusterSharding(context.system).shardRegion(DailyPriceAggregateActor.shardName)

  override def receive: Receive = {
    case SavePriceForRange(userId, unitId, from, to, price) => {
      val fromDate = new DateTime(from).toDateTime(DateTimeZone.UTC)
      val toDate = new DateTime(to).toDateTime(DateTimeZone.UTC)
      val duration = Days.daysBetween(fromDate.toLocalDate, toDate.toLocalDate).getDays

      (0 until duration).foreach(daysFromStart => {
        val day = new DateTime(from).toDateTime(DateTimeZone.UTC).plusDays(daysFromStart).getMillis

        postRegion ! SavePriceForSingleDay(userId, unitId, day, price)
      })
    }
    case Terminated(ref) => as = as filterNot { case (_, v) => v == ref }
  }

}
