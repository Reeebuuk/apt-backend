package hr.com.blanka.apartments.command.booking

import akka.actor.{ActorLogging, Props}
import akka.persistence.PersistentActor
import org.joda.time.DateTime

object EnquiryAggregateActor {
  def apply() = Props(classOf[EnquiryAggregateActor])

  val persistenceId = "EnquiryAggregateActor"
}

class EnquiryAggregateActor extends PersistentActor with ActorLogging {
  override def receiveRecover: Receive = {
    case _ =>
  }

  override def receiveCommand: Receive = {
    case SaveEnquiry(booking) => persist(EnquirySaved(booking, new DateTime())) {
      event => event
    }
  }

  override def persistenceId: String = EnquiryAggregateActor.persistenceId
}
