package hr.com.blanka.apartments.query.contact

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.persistence.{ PersistentActor, RecoveryCompleted }
import hr.com.blanka.apartments.command.booking.{
  BookingAggregateActor,
  EnquiryBooked,
  EnquirySaved
}
import hr.com.blanka.apartments.common.{ Enquiry, HardcodedUnits }
import hr.com.blanka.apartments.query.booking.StartSync
import hr.com.blanka.apartments.query.{ PersistenceOffsetSaved, PersistenceQueryEvent }

import scala.language.postfixOps

object QueryBookingsForEmailsActor {
  def apply(commandSideReaderActor: ActorRef, emailSenderActor: ActorRef, fromEmail: String) =
    Props(classOf[QueryBookingsForEmailsActor], commandSideReaderActor, emailSenderActor, fromEmail)
}

class QueryBookingsForEmailsActor(commandSideReaderActor: ActorRef,
                                  emailSenderActor: ActorRef,
                                  fromEmail: String)
    extends PersistentActor
    with ActorLogging {

  var persistenceOffset: Long = 0

  def enquiryReceived(enquiry: Enquiry): String =
    s"""Hello ${enquiry.name},
       |
       |we are very glad that you decided to spend your holidays with us. :)
       |
       |Apartment : ${HardcodedUnits.units(enquiry.unitId)}
       |From : ${enquiry.dateFrom}
       |To : ${enquiry.dateTo}
       |
       |Don't worry, we usually respond straight away :)
       |
       |Cheers,
       |Kruno
       |
       |www.apartments-blanka.com.hr
     """.stripMargin

  def enquiryBooked(enquiry: Enquiry, depositAmount: BigDecimal, currency: String): String =
    s"""Hello ${enquiry.name},
       |
       |we have received your deposit so you can consider your stay in our place as booked. :)
       |
       |Apartment : ${HardcodedUnits.units(enquiry.unitId)}
       |From : ${enquiry.dateFrom}
       |To : ${enquiry.dateTo}
       |
       |Deposit paid: ${depositAmount.toString()}$currency
       |
       |Cheers,
       |Kruno
       |
       |www.apartments-blanka.com.hr
     """.stripMargin

  override def receiveCommand: Receive = {
    case PersistenceQueryEvent(offset, event: EnquirySaved) =>
      emailSenderActor ! SendEmail(
        from = fromEmail,
        to = List(event.enquiry.email, fromEmail),
        subject = "Apartments Blanka booking request",
        text = enquiryReceived(event.enquiry),
        persistenceOffset = offset
      )
    case PersistenceQueryEvent(offset, event: EnquiryBooked) =>
      emailSenderActor ! SendEmail(
        from = fromEmail,
        to = List(event.enquiry.email, fromEmail),
        subject = "Apartments Blanka booking deposit received",
        text = enquiryBooked(event.enquiry, event.depositAmount, event.currency),
        persistenceOffset = offset
      )
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
      commandSideReaderActor ! StartSync(self,
                                         BookingAggregateActor.persistenceId,
                                         persistenceOffset)
  }

  override def persistenceId: String = "QueryBookingsForEmailsActor"
}
