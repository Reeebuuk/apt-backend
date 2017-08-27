package hr.com.blanka.apartments.query.booking

import akka.actor.{ Actor, ActorLogging, Props }
import hr.com.blanka.apartments.common.ValueClasses.BookingId
import hr.com.blanka.apartments.command.booking.{ BookingAggregateActor, EnquiryReceived }
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import org.scalactic.Good

import scala.language.postfixOps

object UnapprovedEnquiriesActor {
  def apply() = Props(classOf[UnapprovedEnquiriesActor])
}

class UnapprovedEnquiriesActor extends Actor with ActorLogging {

  // temp solution, saving to DB and keeping offset would be more perm solution
  var bookings: Map[BookingId, Booking] = Map.empty

  override def receive: Receive = {
    case PersistenceQueryEvent(_, es: EnquiryReceived) =>
    case _: GetAllBookings =>
      sender() ! Good(AllBookings(bookings.values.toList.sortBy(_.bookingId.id)))
  }

  override def preStart(): Unit = {
    context.parent ! StartSync(self, BookingAggregateActor.persistenceId, 0)
    super.preStart()
  }

}
