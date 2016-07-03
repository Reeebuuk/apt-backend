package hr.com.blanka.apartments.query.booking

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.contrib.persistence.mongodb.{MongoReadJournal, ScalaDslMongoReadJournal}
import akka.persistence.query.{EventEnvelope, PersistenceQuery}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import hr.com.blanka.apartments.command.booking.{BookingAggregateActor, BookingSaved}

object BookedDatesActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[BookedDatesActor], materializer)
}

class BookedDatesActor(implicit materializer: ActorMaterializer) extends Actor with ActorLogging {

  def startSync(actor: ActorRef) = {
    val queries = PersistenceQuery(context.system).readJournalFor[ScalaDslMongoReadJournal](MongoReadJournal.Identifier)

    val src: Source[EventEnvelope, NotUsed] =
      queries.eventsByPersistenceId(BookingAggregateActor.persistenceId, 0L, Long.MaxValue)

    src.runForeach(actor ! _.event)
  }

  override def preStart() = startSync(self)

  override def receive: Receive = {
    case bs : BookingSaved =>
  }
}