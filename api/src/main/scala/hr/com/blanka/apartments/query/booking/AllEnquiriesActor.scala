package hr.com.blanka.apartments.query.booking

import java.time.LocalDateTime

import akka.actor.{ Actor, ActorLogging, Props }
import hr.com.blanka.apartments.command.booking.{
  BookingAggregateActor,
  EnquiryApproved,
  EnquiryBooked,
  EnquiryReceived
}
import hr.com.blanka.apartments.common.Enquiry
import hr.com.blanka.apartments.common.ValueClasses.EnquiryId
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import hr.com.blanka.apartments.query.booking.AllEnquiriesActor.InternalBooking
import hr.com.blanka.apartments.query.price.{ LookupPriceForRange, PriceForRangeCalculated }
import org.scalactic.Good

import scala.language.postfixOps

object AllEnquiriesActor {
  def apply() = Props(classOf[AllEnquiriesActor])

  case class InternalBooking(enquiryId: EnquiryId,
                             enquiryDttm: LocalDateTime,
                             enquiry: Enquiry,
                             totalAmount: Option[BigDecimal],
                             approvedDttm: Option[LocalDateTime] = None,
                             bookingDeposit: Option[BookingDeposit] = None) {
    def toBooking: Option[BookedEnquiry] =
      (approvedDttm, bookingDeposit, totalAmount) match {
        case (Some(ad), Some(bd), Some(ta)) =>
          Some(BookedEnquiry(enquiryId, enquiryDttm, enquiry, ta, ad, bd))
        case _ => None
      }

    def toApprovedEnquiry: Option[ApprovedEnquiry] =
      (approvedDttm, bookingDeposit, totalAmount) match {
        case (Some(appDttm), None, Some(ta)) =>
          Some(ApprovedEnquiry(enquiryId, enquiryDttm, enquiry, ta, appDttm))
        case _ => None
      }

    def toUnapprovedEnquiry: Option[UnapprovedEnquiry] =
      (approvedDttm, totalAmount) match {
        case (None, Some(ta)) => Some(UnapprovedEnquiry(enquiryId, enquiryDttm, enquiry, ta))
        case _                => None
      }
  }

}

class AllEnquiriesActor extends Actor with ActorLogging {

  override def receive: Receive = init(Map.empty)

  def init(bookings: Map[EnquiryId, InternalBooking]): Receive = {
    case PriceForRangeCalculated(enquiryId, price) =>
      enquiryId.foreach { id =>
        bookings.get(id) match {
          case None =>
            log.info(s"PriceForRangeCalculated received without having an enquiry ${id.id}")
          case Some(booking) =>
            log.info(price.toString())
            context become init(
              bookings.updated(id, booking.copy(totalAmount = Some(price)))
            )
        }
      }
    case PersistenceQueryEvent(_, es: EnquiryReceived) =>
      context.parent ! LookupPriceForRange(Some(es.enquiryId),
                                           es.userId,
                                           es.enquiry.unitId,
                                           es.enquiry.dateFrom,
                                           es.enquiry.dateTo,
                                           es.timeSaved)
      context become init(
        bookings.updated(
          es.enquiryId,
          InternalBooking(enquiryId = es.enquiryId,
                          enquiryDttm = es.timeSaved,
                          enquiry = es.enquiry,
                          totalAmount = None)
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
}
