package hr.com.blanka.apartments.price

import akka.actor.Props
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.price.DailyPriceAggregateActor._
import hr.com.blanka.apartments.price.protocol.{PriceDayFetched, LookupPriceForDay, SavePriceForSingleDay, Command}
import org.joda.time.DateTime

case class Price(value: Int, timeSaved: Long)

object Price {
  def apply(value: Int) = new Price(value, DateTime.now().getMillis)
}

case class DailyPriceSaved(unitId: Int, day: Long, newPrice: Price)

object DailyPriceAggregateActor {

  def apply() = Props(new DailyPriceAggregateActor())

  val idExtractor: ShardRegion.ExtractEntityId = {
    case cmd: Command => (cmd.userId, cmd)
  }

  val shardResolver: ShardRegion.ExtractShardId = {
    case cmd: Command => "Main"
  }

  val shardName: String = "DailyPriceAggregateActor"
}

class DailyPriceAggregateActor extends PersistentActor {

  override def persistenceId: String = self.path.parent.name + "-" + self.path.name

  val queryPriceRangeActor = context.actorOf(QueryPriceRangeActor(), "queryActor")

  def updateState(dailyPriceSaved: DailyPriceSaved, dailyPrices: Map[Int, Map[Long, List[Price]]]): Unit = {
    import dailyPriceSaved._

    val newDailyPrices: Map[Long, List[Price]] = dailyPrices.get(unitId) match {
      case None => Map(day -> List(newPrice))
      case Some(previousPricesForDay) =>
        previousPricesForDay.get(day) match {
          case None => Map(day -> List(newPrice))
          case Some(previousPrices) => Map(day -> previousPrices.+:(newPrice))
        }
    }

    context become active(dailyPrices + (unitId -> newDailyPrices))
  }

  override def receiveCommand = active(Map[Int, Map[Long, List[Price]]]())

  def active(dailyPrices: Map[Int, Map[Long, List[Price]]]): Receive = {
    case SavePriceForSingleDay(userId, unitId, day, price) => {
      persist(DailyPriceSaved(unitId, day, Price(price)))(updateState(_, dailyPrices))
    }
    case LookupPriceForDay(_, unitId, requestId, day) => {
      val lastPrice: Int = dailyPrices.get(unitId) match {
        case None => 0
        case Some(priceForDay) =>
          priceForDay.get(day) match {
            case None => 0
            case Some(prices) => prices.head.value
        }
      }

      queryPriceRangeActor ! PriceDayFetched(requestId, day, lastPrice)
    }
  }

  override def receiveRecover: Receive = ???
}
