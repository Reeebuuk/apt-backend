package hr.com.blanka.apartments.query.contact

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.persistence.{ PersistentActor, RecoveryCompleted }
import hr.com.blanka.apartments.command.booking.{
  BookingAggregateActor,
  EnquiryBooked,
  EnquiryReceived
}
import hr.com.blanka.apartments.common.ValueClasses.EnquiryId
import hr.com.blanka.apartments.common.{ Enquiry, HardcodedUnits }
import hr.com.blanka.apartments.query.booking.StartSync
import hr.com.blanka.apartments.query.{ PersistenceOffsetSaved, PersistenceQueryEvent }

import scala.language.postfixOps

object QueryBookingsForEmailsActor {
  def apply(emailSenderActor: ActorRef, fromEmail: String) =
    Props(classOf[QueryBookingsForEmailsActor], emailSenderActor, fromEmail)

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
}

class QueryBookingsForEmailsActor(emailSenderActor: ActorRef, fromEmail: String)
    extends PersistentActor
    with ActorLogging {

  import QueryBookingsForEmailsActor._

  var persistenceOffset: Long = 0

  var bookings: Map[EnquiryId, Enquiry] = Map.empty

  override def receiveCommand: Receive = {
    case PersistenceQueryEvent(offset, event: EnquiryReceived) =>
      log.info("Email request from enquiry saved")
      log.debug(event.toString)
      emailSenderActor ! SendEmail(
        from = fromEmail,
        to = List(event.enquiry.email, fromEmail),
        subject = "Apartments Blanka booking request",
        text = enquiryReceived(event.enquiry),
        persistenceOffset = offset
      )

      bookings = bookings + (event.enquiryId -> event.enquiry)

    case PersistenceQueryEvent(offset, event: EnquiryBooked) =>
      log.info("Email request from enquiry booked")
      log.debug(event.toString)
      bookings
        .get(event.enquiryId)
        .foreach(
          enquiry =>
            emailSenderActor ! SendEmail(
              from = fromEmail,
              to = List(enquiry.email, fromEmail),
              subject = "Apartments Blanka booking deposit received",
              text = enquiryBooked(enquiry, event.depositAmount, event.currency),
              persistenceOffset = offset
          )
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
      context.parent ! StartSync(self, BookingAggregateActor.persistenceId, persistenceOffset)
  }

  override def persistenceId: String = "QueryBookingsForEmailsActor"
}
