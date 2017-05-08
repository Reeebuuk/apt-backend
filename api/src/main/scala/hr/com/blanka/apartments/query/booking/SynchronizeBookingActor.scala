package hr.com.blanka.apartments.query.booking

import akka.NotUsed
import akka.actor.{ Actor, ActorRef, Props }
import akka.contrib.persistence.mongodb.{ MongoReadJournal, ScalaDslMongoReadJournal }
import akka.persistence.query.{ EventEnvelope, PersistenceQuery }
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

object SynchronizeBookingActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[SynchronizeBookingActor], materializer)
}

class SynchronizeBookingActor(implicit materializer: ActorMaterializer) extends Actor {

  def startSync(actor: ActorRef, persistenceId: String, initialIndex: Long) = {
    val queries = PersistenceQuery(context.system).readJournalFor[ScalaDslMongoReadJournal](MongoReadJournal.Identifier)

    val src: Source[EventEnvelope, NotUsed] =
      queries.eventsByPersistenceId(persistenceId, initialIndex, Long.MaxValue)

    src.runForeach(e => actor ! EnquiryBookedWithSeqNmr(e.sequenceNr, e.event))
  }

  override def receive: Receive = {
    case StartSync(actor, persistenceId, initialIndex) =>
      startSync(actor, persistenceId, initialIndex)
  }
}
