package hr.com.blanka.apartments.query

import akka.Done
import akka.actor.{ Actor, ActorRef, Props }
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.ActorMaterializer
import hr.com.blanka.apartments.command.price.PriceAggregateActor

import scala.concurrent.Future

object QueryProjectionSupervisor {
  def apply(materializer: ActorMaterializer) =
    Props(classOf[QueryProjectionSupervisor], materializer)
}

class QueryProjectionSupervisor(implicit materializer: ActorMaterializer) extends Actor {

  def startSync(actor: ActorRef): Future[Done] = {
    val queries =
      PersistenceQuery(context.system)
        .readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

    val src =
      queries.eventsByPersistenceId(PriceAggregateActor.persistenceId, 0L, Long.MaxValue)

    src.runForeach(actor ! _.event)
  }

  override def preStart(): Unit = startSync(context.parent)

  override def receive: Receive = { case _ => }
}
