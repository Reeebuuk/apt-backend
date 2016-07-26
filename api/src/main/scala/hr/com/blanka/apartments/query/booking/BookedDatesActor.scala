package hr.com.blanka.apartments.query.booking

import akka.actor.{ActorLogging, Props}
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.command.booking.{BookingAggregateActor, EnquiryBooked}
import org.joda.time.{Days, LocalDate}
import org.scalactic.Good

object BookedDatesActor {
  def apply() = Props(classOf[BookedDatesActor])

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e: EnquiryBooked => (e.userId.toString, e)
    case e: GetBookedDates => (e.userId.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case _ => "three"
  }
}

case class PeriodBooked(unitId: Int, period: List[BookedDay], sequenceNmbr: Long)

class BookedDatesActor extends PersistentActor with ActorLogging {

  var bookedDatesPerUnit = Map[Int, List[BookedDay]]()
  var sequenceNmbr: Long = 0

  override def receiveCommand: Receive = {
    case EnquiryBookedWithSeqNmr(nmbr, EnquiryBooked(userId, _, enquiry, _, _, _)) =>
      val fromDate = new LocalDate(enquiry.dateFrom)
      val toDate = new LocalDate(enquiry.dateTo)

      val currentlyBookedDates: List[BookedDay] = bookedDatesPerUnit.getOrElse(enquiry.unitId, List.empty)
      val currentlyBookedDatesOnly: List[LocalDate] = currentlyBookedDates.map(_.day)

      val bookedPeriod: List[BookedDay] = iterateThroughDays(fromDate, toDate).map {
        case day if day == fromDate && !currentlyBookedDatesOnly.contains(day) => BookedDay(day, firstDay = true, lastDay = false)
        case day if day == toDate && !currentlyBookedDatesOnly.contains(day) => BookedDay(day, firstDay = false, lastDay = true)
        case day => BookedDay(day, firstDay = false, lastDay = false)
      }

      persist(PeriodBooked(enquiry.unitId, bookedPeriod, nmbr)) { event =>
        bookedDatesPerUnit = bookedDatesPerUnit + (event.unitId -> (currentlyBookedDates ++ event.period).distinct)
        sequenceNmbr = nmbr
      }
    case GetBookedDates(_, unitId) => sender() ! Good(BookedDays(bookedDatesPerUnit.getOrElse(unitId, List.empty)))
  }

  override def receiveRecover: Receive = {
    case PeriodBooked(unitId, bookedPeriod, nmbr) =>
      val currentlyBookedDates: List[BookedDay] = bookedDatesPerUnit.getOrElse(unitId, List.empty)
      bookedDatesPerUnit = bookedDatesPerUnit + (unitId -> (currentlyBookedDates ++ bookedPeriod).distinct)
      sequenceNmbr = nmbr
  }

  def iterateThroughDays(from: LocalDate, to: LocalDate): List[LocalDate] = {
    (0 to Days.daysBetween(from, to).getDays).map(from.plusDays).toList
  }

  override def preStart() = {
    context.parent ! StartSync(self, BookingAggregateActor.persistenceId, sequenceNmbr)
  }

  override def persistenceId: String = "BookedDatesActor"
}