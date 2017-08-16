package hr.com.blanka.apartments.query.booking

import akka.Done
import akka.actor.{ Actor, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.ActorMaterializer

import scala.concurrent.Future

object SynchronizeBookingActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[SynchronizeBookingActor], materializer)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e: StartSync => (e.actor.path.name, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = _ => "two"
}

class SynchronizeBookingActor(implicit materializer: ActorMaterializer) extends Actor {

  def startSync(actor: ActorRef, persistenceId: String, initialIndex: Long): Future[Done] = {
    val queries = PersistenceQuery(context.system)
      .readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

    val src =
      queries.eventsByPersistenceId(persistenceId, initialIndex, Long.MaxValue)

    src.runForeach(e => actor ! EnquiryBookedWithSequenceNumber(e.sequenceNr, e.event))
  }

  override def receive: Receive = {
    case StartSync(actor, persistenceId, initialIndex) =>
      startSync(actor, persistenceId, initialIndex)
  }
}
