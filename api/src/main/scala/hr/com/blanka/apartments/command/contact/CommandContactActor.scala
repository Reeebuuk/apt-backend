package hr.com.blanka.apartments.command.contact

import akka.actor.{ ActorLogging, Props }
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.utils.PredefinedTimeout
import org.scalactic.Good

import scala.language.postfixOps

object CommandContactActor {
  def apply() = Props(classOf[CommandContactActor])

  val persistenceId: String = "CommandContactActor"
}

class CommandContactActor extends PersistentActor with ActorLogging with PredefinedTimeout {

  override def receiveCommand: Receive = {
    case SaveContact(name, email, text) =>
      persist(ContactSaved(name, email, text)) { _ =>
        sender() ! Good
      }
  }

  override def receiveRecover: Receive = {
    case _ =>
  }

  override def persistenceId: String = CommandContactActor.persistenceId
}
