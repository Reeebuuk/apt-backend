package hr.com.blanka.apartments.query.price

import akka.actor.{ Actor, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.command.price.{ DailyPriceSaved, PriceAggregateActor }
import hr.com.blanka.apartments.common.DayMonth
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import hr.com.blanka.apartments.query.booking.StartSync

object DailyPriceAggregateActor {
  def apply(commandSideReaderActor: ActorRef) =
    Props(classOf[DailyPriceAggregateActor], commandSideReaderActor)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e @ DailyPriceSaved(userId, unitId, _, _, _) => (s"${userId.id}${unitId.id}", e)
    case e @ LookupPriceForDay(userId, unitId, _)     => (s"${userId.id}${unitId.id}", e)
  }

  val extractShardId: ShardRegion.ExtractShardId = _ => "one"
}

class DailyPriceAggregateActor(commandSideReaderActor: ActorRef) extends Actor {

  def updateState(newDailyPrice: DailyPriceSaved,
                  currentDailyPrices: Map[DayMonth, List[BigDecimal]]): Unit = {

    val newPrices: List[BigDecimal] = currentDailyPrices.get(newDailyPrice.dayMonth) match {
      case Some(priceForDay) => newDailyPrice.price :: priceForDay
      case None              => List(newDailyPrice.price)
    }

    context become active(currentDailyPrices + (newDailyPrice.dayMonth -> newPrices))
  }

  override def receive: Receive = active(Map[DayMonth, List[BigDecimal]]())

  def active(currentDailyPrices: Map[DayMonth, List[BigDecimal]]): Receive = {
    case PersistenceQueryEvent(_, e: DailyPriceSaved) =>
      updateState(e, currentDailyPrices)

    case LookupPriceForDay(userId, unitId, day) =>
      val lastPrice: BigDecimal = currentDailyPrices.get(day) match {
        case None        => 0
        case Some(price) => price.head
      }

      sender() ! PriceDayFetched(lastPrice)
  }

  override def preStart(): Unit = {
    commandSideReaderActor ! StartSync(self, PriceAggregateActor.persistenceId, 0)
    super.preStart()
  }
}
