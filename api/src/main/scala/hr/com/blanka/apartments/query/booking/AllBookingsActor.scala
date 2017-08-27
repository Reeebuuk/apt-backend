package hr.com.blanka.apartments.query.booking

import java.time.LocalDateTime

import akka.actor.{ Actor, ActorLogging, Props }
import hr.com.blanka.apartments.common.ValueClasses.BookingId
import hr.com.blanka.apartments.command.booking.{
  BookingAggregateActor,
  EnquiryApproved,
  EnquiryBooked,
  EnquiryReceived
}
import hr.com.blanka.apartments.common.Enquiry
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import hr.com.blanka.apartments.query.booking.AllBookingsActor.InternalBooking
import org.scalactic.Good

import scala.language.postfixOps

object AllBookingsActor {
  def apply() = Props(classOf[AllBookingsActor])

  case class InternalBooking(bookingId: BookingId,
                             enquiryDttm: LocalDateTime,
                             enquiry: Enquiry,
                             approvedDttm: Option[LocalDateTime] = None,
                             bookingDeposit: Option[BookingDeposit] = None) {
    def toBooking: Option[Booking] =
      (approvedDttm, bookingDeposit) match {
        case (Some(ad), Some(bd)) => Some(Booking(bookingId, enquiryDttm, enquiry, ad, bd))
        case (None, Some(bd))     => Some(Booking(bookingId, enquiryDttm, enquiry, enquiryDttm, bd))
        case _                    => None
      }
  }

}

class AllBookingsActor extends Actor with ActorLogging {

  override def receive: Receive = init(Map.empty)

  def init(bookings: Map[BookingId, InternalBooking]): Receive = {
    case PersistenceQueryEvent(_, es: EnquiryReceived) =>
      context become init(
        bookings.updated(
          es.bookingId,
          InternalBooking(bookingId = es.bookingId,
                          enquiryDttm = es.timeSaved,
                          enquiry = es.enquiry)
        )
      )
    case PersistenceQueryEvent(_, es: EnquiryApproved) =>
      bookings.get(es.bookingId) match {
        case None =>
          log.info(s"Approval received without having an enquiry ${es.bookingId.id}")
          log.debug(es.toString)
        case Some(booking) =>
          context become init(
            bookings.updated(es.bookingId, booking.copy(approvedDttm = Some(es.timeSaved)))
          )
      }

    case PersistenceQueryEvent(_, eb: EnquiryBooked) =>
      bookings.get(eb.bookingId) match {
        case None =>
          log.info(s"Deposit received without having an enquiry ${eb.bookingId.id}")
          log.debug(eb.toString)
        case Some(booking) =>
          context become init(
            bookings.updated(eb.bookingId,
                             booking.copy(
                               bookingDeposit = Some(
                                 BookingDeposit(
                                   amount = eb.depositAmount,
                                   currency = eb.currency,
                                   when = eb.timeSaved
                                 )
                               )
                             ))
          )
      }
    case _: GetAllBookings =>
      val b = bookings.values.toList.sortBy(_.bookingId.id).flatMap(_.toBooking)
      sender() ! Good(AllBookings(b))
  }

  override def preStart(): Unit = {
    context.parent ! StartSync(self, BookingAggregateActor.persistenceId, 0)
    super.preStart()
  }

}
