package hr.com.blanka.apartments.query.booking

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.persistence.query.journal.leveldb.scaladsl.LeveldbReadJournal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import hr.com.blanka.apartments.command.booking.BookingAggregateActor

object QueryBookingActor {
  def apply() = Props(classOf[QueryBookingActor])
}

class QueryBookingActor extends Actor with ActorLogging {

/*  def startSync(actor: ActorRef, system: ActorSystem, mat: ActorMaterializer) = {
    val queries = PersistenceQuery(system).readJournalFor[LeveldbReadJournal](
      LeveldbReadJournal.Identifier)

    val src: Source[EventEnvelope, NotUsed] =
      queries.eventsByPersistenceId(BookingAggregateActor.persistenceId, 0L, Long.MaxValue)

    src.runForeach(actor ! _.event)
  }*/

  val bookedDatesActor = context.actorOf(BookedDatesActor(), "BookedDatesActor")
  val unitAvailabilityActor = context.actorOf(UnitAvailabilityActor(), "unitAvailabilityActor")

  override def receive: Receive = {
    case _ =>
  }
}
