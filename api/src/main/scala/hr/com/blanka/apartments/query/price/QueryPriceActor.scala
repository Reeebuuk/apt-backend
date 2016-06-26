package hr.com.blanka.apartments.query.price

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import hr.com.blanka.apartments.command.price.PriceAggregateActor
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps

object QueryPriceActor {
  def apply(system: ActorSystem) = Props(classOf[QueryPriceActor], system)
}

class QueryPriceActor(system: ActorSystem) extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  def startSync(actor: ActorRef)(implicit mat: ActorMaterializer) = {
    val queries = PersistenceQuery(system).readJournalFor[LeveldbReadJournal](
      LeveldbReadJournal.Identifier)

    val src: Source[EventEnvelope, NotUsed] =
      queries.eventsByPersistenceId(PriceAggregateActor.persistenceId, 0L, Long.MaxValue)

    src.runForeach(actor ! _.event)
  }

  val dailyPriceAggregateActor = context.actorOf(DailyPriceAggregateActor(), "DailyPriceAggregateActor")
  val queryPriceRangeActor = context.actorOf(QueryPriceRangeActor(dailyPriceAggregateActor), "QueryPriceRangeActor")

  override def preStart() = startSync(dailyPriceAggregateActor)

  override def receive: Receive = {
    case e :LookupPriceForRange =>
      queryPriceRangeActor ? e
  }
}
