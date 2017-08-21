package hr.com.blanka.apartments.query.contact

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.persistence.{ PersistentActor, RecoveryCompleted }
import hr.com.blanka.apartments.command.contact.{ CommandContactActor, ContactSaved }
import hr.com.blanka.apartments.query.booking.StartSync
import hr.com.blanka.apartments.query.{ PersistenceOffsetSaved, PersistenceQueryEvent }

import scala.language.postfixOps

object QueryContactEmailsActor {
  def apply(commandSideReaderActor: ActorRef, emailSenderActor: ActorRef, fromEmail: String) =
    Props(classOf[QueryContactEmailsActor], commandSideReaderActor, emailSenderActor, fromEmail)
}

class QueryContactEmailsActor(commandSideReaderActor: ActorRef,
                              emailSenderActor: ActorRef,
                              fromEmail: String)
    extends PersistentActor
    with ActorLogging {

  var persistenceOffset: Long = 0

  def emailTemplate(name: String, text: String): String =
    s"""Hello $name,
       |
       |We have received your message:
       |
       |"$text"
       |
       |Don't worry, we usually respond straight away :)
       |
       |Cheers,
       |Kruno
       |
       |www.apartments-blanka.com.hr
     """.stripMargin

  override def receiveCommand: Receive = {
    case PersistenceQueryEvent(offset, cs: ContactSaved) =>
      emailSenderActor ! SendEmail(fromEmail,
                                   List(cs.email, fromEmail),
                                   "Apartments Blanka contact",
                                   emailTemplate(cs.name, cs.text),
                                   offset)
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
      commandSideReaderActor ! StartSync(self, CommandContactActor.persistenceId, persistenceOffset)
  }

  override def persistenceId: String = "QueryContactEmailsActor"
}
