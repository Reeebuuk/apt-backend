package hr.com.blanka.apartments.booking

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import hr.com.blanka.apartments.booking.protocol.BookingSaved

object UnitAvailabilityQueryActor {
  def apply() = Props(classOf[UnitAvailabilityQueryActor])

  def startSync(actor: ActorRef, system: ActorSystem, mat: ActorMaterializer) = {
    val queries = PersistenceQuery(system).readJournalFor[LeveldbReadJournal](
      LeveldbReadJournal.Identifier)

    val src: Source[EventEnvelope, NotUsed] =
      queries.eventsByPersistenceId(CommandBookingActor.persistenceId, 0L, Long.MaxValue)

    src.runForeach(actor ! _.event)
  }
}

class UnitAvailabilityQueryActor extends Actor with ActorLogging {

  override def receive: Receive = {
    case bs : BookingSaved =>
  }
}
