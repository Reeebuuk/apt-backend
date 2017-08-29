package hr.com.blanka.apartments.query.booking

import java.time.LocalDateTime

import akka.actor.{ Actor, ActorLogging, Props }
import hr.com.blanka.apartments.common.ValueClasses.EnquiryId
import hr.com.blanka.apartments.command.booking.{
  BookingAggregateActor,
  EnquiryApproved,
  EnquiryBooked,
  EnquiryReceived
}
import hr.com.blanka.apartments.common.Enquiry
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import hr.com.blanka.apartments.query.booking.AllEnquiriesActor.InternalBooking
import org.scalactic.Good

import scala.language.postfixOps

object AllEnquiriesActor {
  def apply() = Props(classOf[AllEnquiriesActor])

  case class InternalBooking(enquiryId: EnquiryId,
                             enquiryDttm: LocalDateTime,
                             enquiry: Enquiry,
                             approvedDttm: Option[LocalDateTime] = None,
                             bookingDeposit: Option[BookingDeposit] = None) {
    def toBooking: Option[BookedEnquiry] =
      (approvedDttm, bookingDeposit) match {
        case (Some(ad), Some(bd)) => Some(BookedEnquiry(enquiryId, enquiryDttm, enquiry, ad, bd))
        case (None, Some(bd)) =>
          Some(BookedEnquiry(enquiryId, enquiryDttm, enquiry, enquiryDttm, bd))
        case _ => None
      }

    def toApprovedEnquiry: Option[ApprovedEnquiry] =
      (approvedDttm, bookingDeposit) match {
        case (Some(appDttm), None) =>
          Some(ApprovedEnquiry(enquiryId, enquiryDttm, enquiry, appDttm))
        case _ => None
      }

    def toUnapprovedEnquiry: Option[UnapprovedEnquiry] =
      approvedDttm match {
        case None => Some(UnapprovedEnquiry(enquiryId, enquiryDttm, enquiry))
        case _    => None
      }
  }

}

class AllEnquiriesActor extends Actor with ActorLogging {

  override def receive: Receive = init(Map.empty)

  def init(bookings: Map[EnquiryId, InternalBooking]): Receive = {
    case PersistenceQueryEvent(_, es: EnquiryReceived) =>
      context become init(
        bookings.updated(
          es.enquiryId,
          InternalBooking(enquiryId = es.enquiryId,
                          enquiryDttm = es.timeSaved,
                          enquiry = es.enquiry)
        )
      )
    case PersistenceQueryEvent(_, es: EnquiryApproved) =>
      bookings.get(es.enquiryId) match {
        case None =>
          log.info(s"Approval received without having an enquiry ${es.enquiryId.id}")
          log.debug(es.toString)
        case Some(booking) =>
          context become init(
            bookings.updated(es.enquiryId, booking.copy(approvedDttm = Some(es.timeSaved)))
          )
      }

    case PersistenceQueryEvent(_, eb: EnquiryBooked) =>
      bookings.get(eb.enquiryId) match {
        case None =>
          log.info(s"Deposit received without having an enquiry ${eb.enquiryId.id}")
          log.debug(eb.toString)
        case Some(booking) =>
          context become init(
            bookings.updated(eb.enquiryId,
                             booking.copy(
                               bookingDeposit = Some(
                                 BookingDeposit(
                                   amount = eb.depositAmount,
                                   currency = eb.depositCurrency,
                                   when = eb.timeSaved
                                 )
                               )
                             ))
          )
      }
    case GetAllUnapprovedEnquiries(_, year) =>
      val b = bookings.values.toList
        .withFilter(_.enquiry.dateFrom.getYear == year)
        .flatMap(_.toUnapprovedEnquiry)
        .sortBy(_.enquiryId.id)
      sender() ! Good(AllUnapprovedEnquiries(b))
    case GetAllApprovedEnquiries(_, year) =>
      val b = bookings.values.toList
        .withFilter(_.enquiry.dateFrom.getYear == year)
        .flatMap(_.toApprovedEnquiry)
        .sortBy(_.enquiryId.id)
      sender() ! Good(AllApprovedEnquiries(b))
    case GetAllBookedEnquiries(_, year) =>
      val b = bookings.values.toList
        .withFilter(_.enquiry.dateFrom.getYear == year)
        .flatMap(_.toBooking)
        .sortBy(_.enquiryId.id)
      sender() ! Good(AllBookedEnquiries(b))
  }

  override def preStart(): Unit = {
    context.parent ! StartSync(self, BookingAggregateActor.persistenceId, 0)
    super.preStart()
  }

}
