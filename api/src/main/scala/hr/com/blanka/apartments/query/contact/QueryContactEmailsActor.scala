package hr.com.blanka.apartments.query.contact

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.persistence.{ PersistentActor, RecoveryCompleted }
import hr.com.blanka.apartments.command.contact.{ CommandContactActor, ContactSaved }
import hr.com.blanka.apartments.query.booking.StartSync
import hr.com.blanka.apartments.query.{ PersistenceOffsetSaved, PersistenceQueryEvent }

import scala.language.postfixOps

object QueryContactEmailsActor {
  def apply(synchronizeBookingActor: ActorRef, emailSenderActor: ActorRef, fromEmail: String) =
    Props(classOf[QueryContactEmailsActor], synchronizeBookingActor, emailSenderActor, fromEmail)
}

class QueryContactEmailsActor(synchronizeBookingActor: ActorRef,
                              emailSenderActor: ActorRef,
                              fromEmail: String)
    extends PersistentActor
    with ActorLogging {

  var persistenceOffset: Long = 0

  override def receiveCommand: Receive = {
    case PersistenceQueryEvent(offset, cs: ContactSaved) =>
      emailSenderActor ! SendEmail(fromEmail, cs.email, "Contact", cs.text, offset)
    case offset: Long =>
      persist(PersistenceOffsetSaved(offset)) { _ =>
        persistenceOffset = offset + 1
      }
  }

  override def receiveRecover: Receive = {
    case PersistenceOffsetSaved(offset) =>
      persistenceOffset = offset
    case RecoveryCompleted =>
      persistenceOffset += 1
      synchronizeBookingActor ! StartSync(self,
                                          CommandContactActor.persistenceId,
                                          persistenceOffset)
  }

  override def persistenceId: String = "QueryContactEmailsActor"
}
