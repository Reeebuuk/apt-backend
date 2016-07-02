package hr.com.blanka.apartments.query.price

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.contrib.persistence.mongodb.{MongoReadJournal, ScalaDslMongoReadJournal}
import akka.pattern.{ask, pipe}
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import hr.com.blanka.apartments.command.price.PriceAggregateActor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object QueryPriceActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[QueryPriceActor], materializer)
}

class QueryPriceActor(implicit materializer: ActorMaterializer) extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  def startSync(actor: ActorRef) = {
    val queries = PersistenceQuery(context.system).readJournalFor[ScalaDslMongoReadJournal](MongoReadJournal.Identifier)

    val src: Source[EventEnvelope, NotUsed] =
      queries.eventsByPersistenceId(PriceAggregateActor.persistenceId, 0L, Long.MaxValue)

    src.runForeach(actor ! _.event)
  }

  val dailyPriceAggregateActor = context.actorOf(DailyPriceAggregateActor(), "DailyPriceAggregateActor")
  val queryPriceRangeActor = context.actorOf(QueryPriceRangeActor(dailyPriceAggregateActor), "QueryPriceRangeActor")

  override def preStart() = startSync(dailyPriceAggregateActor)

  override def receive: Receive = {
    case e :LookupPriceForRange =>
      val msgSender = sender()
      queryPriceRangeActor ? e pipeTo msgSender
  }
}
