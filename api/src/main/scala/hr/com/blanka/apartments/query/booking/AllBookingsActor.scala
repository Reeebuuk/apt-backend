package hr.com.blanka.apartments.query.booking

import akka.actor.{ Actor, ActorLogging, Props }
import hr.com.blanka.apartments.common.ValueClasses.BookingId
import hr.com.blanka.apartments.command.booking.{
  BookingAggregateActor,
  EnquiryBooked,
  EnquirySaved
}
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import org.scalactic.Good

import scala.language.postfixOps

object AllBookingsActor {
  def apply() = Props(classOf[AllBookingsActor])
}

class AllBookingsActor extends Actor with ActorLogging {

  // temp solution, saving to DB and keeping offset would be more perm solution
  var bookings: Map[BookingId, Booking] = Map.empty

  override def receive: Receive = {
    case PersistenceQueryEvent(_, es: EnquirySaved) =>
      bookings = bookings.updated(es.bookingId, Booking(es.bookingId, es.timeSaved, es.enquiry))
    case PersistenceQueryEvent(_, eb: EnquiryBooked) =>
      bookings = bookings.updated(
        eb.bookingId,
        Booking(eb.bookingId,
                eb.timeSaved,
                eb.enquiry,
                Some(BookingDeposit(eb.depositAmount, eb.currency, eb.timeSaved)))
      )
    case _: GetAllBookings =>
      sender() ! Good(AllBookings(bookings.values.toList))
  }

  override def preStart(): Unit = {
    context.parent ! StartSync(self, BookingAggregateActor.persistenceId, 0)
    super.preStart()
  }

}
