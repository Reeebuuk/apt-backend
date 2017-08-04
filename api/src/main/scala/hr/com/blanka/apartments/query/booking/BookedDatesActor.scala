package hr.com.blanka.apartments.query.booking

import java.time.LocalDate

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.command.booking.{ BookingAggregateActor, EnquiryBooked }
import hr.com.blanka.apartments.utils.HelperMethods
import org.scalactic.Good

object BookedDatesActor {
  def apply(synchronizeBookingActor: ActorRef) = Props(classOf[BookedDatesActor], synchronizeBookingActor)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e: EnquiryBooked => (e.userId.toString, e)
    case e: GetBookedDates => (e.userId.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case _ => "three"
  }
}

case class PeriodBooked(unitId: Int, period: List[BookedDay], sequenceNmbr: Long)

class BookedDatesActor(synchronizeBookingActor: ActorRef) extends PersistentActor with HelperMethods with ActorLogging {

  var bookedDatesPerUnit = Map[Int, List[BookedDay]]()
  var recoverySequenceNumberForQuery: Long = 0

  override def receiveCommand: Receive = {
    case EnquiryBookedWithSeqNmr(nmbr, EnquiryBooked(userId, _, enquiry, _, _, _)) =>
      val currentlyBookedDates: List[BookedDay] = bookedDatesPerUnit.getOrElse(enquiry.unitId, List.empty)
      val currentlyBookedDatesOnly: List[LocalDate] = currentlyBookedDates.map(_.day)

      val bookedPeriod: List[BookedDay] = iterateThroughDays(enquiry.dateFrom, enquiry.dateTo).map {
        case day if day == enquiry.dateFrom && !currentlyBookedDatesOnly.contains(day) =>
          BookedDay(day, firstDay = true, lastDay = false)
        case day if day == enquiry.dateTo && !currentlyBookedDatesOnly.contains(day) =>
          BookedDay(day, firstDay = false, lastDay = true)
        case day => BookedDay(day, firstDay = false, lastDay = false)
      }

      persist(PeriodBooked(enquiry.unitId, bookedPeriod, nmbr)) { event =>
        bookedDatesPerUnit = bookedDatesPerUnit + (event.unitId -> (currentlyBookedDates ++ event.period).distinct)
        recoverySequenceNumberForQuery = nmbr
      }
    case GetBookedDates(_, unitId) => sender() ! Good(BookedDays(bookedDatesPerUnit.getOrElse(unitId, List.empty)))
  }

  override def receiveRecover: Receive = {
    case PeriodBooked(unitId, bookedPeriod, nmbr) =>
      val currentlyBookedDates: List[BookedDay] = bookedDatesPerUnit.getOrElse(unitId, List.empty)
      bookedDatesPerUnit = bookedDatesPerUnit + (unitId -> (currentlyBookedDates ++ bookedPeriod).distinct)
      recoverySequenceNumberForQuery = nmbr
  }

  override def preStart() =
    synchronizeBookingActor ! StartSync(self, BookingAggregateActor.persistenceId, recoverySequenceNumberForQuery)

  override def persistenceId: String = "BookedDatesActor"
}
