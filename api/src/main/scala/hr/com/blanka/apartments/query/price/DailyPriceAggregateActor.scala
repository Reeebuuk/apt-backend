package hr.com.blanka.apartments.query.price

import akka.NotUsed
import akka.actor.{Actor, ActorRef, Props}
import akka.contrib.persistence.mongodb.{MongoReadJournal, ScalaDslMongoReadJournal}
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import hr.com.blanka.apartments.command.price.{DailyPriceSaved, DayMonth, PriceAggregateActor}

object DailyPriceAggregateActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[DailyPriceAggregateActor], materializer)
}

class DailyPriceAggregateActor(implicit materializer: ActorMaterializer) extends Actor {

  def startSync(actor: ActorRef) = {
    val queries = PersistenceQuery(context.system).readJournalFor[ScalaDslMongoReadJournal](MongoReadJournal.Identifier)

    val src: Source[EventEnvelope, NotUsed] =
      queries.eventsByPersistenceId(PriceAggregateActor.persistenceId, 0L, Long.MaxValue)

    src.runForeach(actor ! _.event)
  }

  override def preStart() = startSync(self)

  def updateState(newDailyPrice: DailyPriceSaved, currentDailyPrices: Map[String, Map[Int, Map[DayMonth, BigDecimal]]]): Unit = {

    val newPrices : Map[String, Map[Int, Map[DayMonth, BigDecimal]]] = currentDailyPrices.get(newDailyPrice.userId) match {
      case None => currentDailyPrices + (newDailyPrice.userId -> Map(newDailyPrice.unitId -> Map(newDailyPrice.dayMonth -> newDailyPrice.price)))
      case Some(currentPricesForUser) => currentPricesForUser.get(newDailyPrice.unitId) match {
        case None => currentDailyPrices + (newDailyPrice.userId -> (currentPricesForUser ++ Map(newDailyPrice.unitId -> Map(newDailyPrice.dayMonth -> newDailyPrice.price))))
        case Some(currentPriceForUnit) =>
          currentDailyPrices + (newDailyPrice.userId -> (currentPricesForUser + (newDailyPrice.unitId -> (currentPriceForUnit + (newDailyPrice.dayMonth -> newDailyPrice.price)))))
      }
    }
    context become active(newPrices)
  }

  override def receive = active(Map[String, Map[Int, Map[DayMonth, BigDecimal]]]())

  def active(currentDailyPrices: Map[String, Map[Int, Map[DayMonth, BigDecimal]]]): Receive = {
    case e : DailyPriceSaved =>
      updateState(e, currentDailyPrices)

    case LookupPriceForDay(userId, unitId, day) =>
      val lastPrice: BigDecimal = currentDailyPrices.get(userId) match {
        case None => 0
        case Some(priceForUnit) => priceForUnit.get(unitId) match {
          case None => 0
          case Some(priceForDay) =>
            priceForDay.get(day) match {
              case None => 0
              case Some(price) => price
            }
        }

      }

      sender() ! PriceDayFetched(lastPrice)
  }

}
