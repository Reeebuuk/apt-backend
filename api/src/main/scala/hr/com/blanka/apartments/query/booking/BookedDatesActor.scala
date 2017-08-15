package hr.com.blanka.apartments.query.booking

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.ValueClasses.UnitId
import hr.com.blanka.apartments.command.booking.{ BookingAggregateActor, Enquiry, EnquiryBooked }
import hr.com.blanka.apartments.utils.HelperMethods
import org.scalactic.Good

object BookedDatesActor {
  def apply(synchronizeBookingActor: ActorRef) =
    Props(classOf[BookedDatesActor], synchronizeBookingActor)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e: EnquiryBooked  => (e.userId.toString, e)
    case e: GetBookedDates => (e.userId.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = _ => "three"

  def markNewDates(currentlyBookedDates: List[BookedDay], enquiry: Enquiry): List[BookedDay] = {

    import HelperMethods._

    iterateThroughDays(enquiry.dateFrom, enquiry.dateTo).map {
      case day if day == enquiry.dateFrom =>
        currentlyBookedDates.find(_.day == day) match {
          case Some(bd) if bd.lastDay || !bd.firstDay =>
            BookedDay(day, firstDay = false, lastDay = false)
          case _ => BookedDay(day, firstDay = true, lastDay = false)
        }
      case day if day == enquiry.dateTo =>
        currentlyBookedDates.find(_.day == day) match {
          case Some(bd) if bd.firstDay || !bd.lastDay =>
            BookedDay(day, firstDay = false, lastDay = false)
          case _ =>
            BookedDay(day, firstDay = false, lastDay = true)
        }
      case day => BookedDay(day, firstDay = false, lastDay = false)
    }
  }

  def mergeIntoExistingSchedule(currentlyBookedDates: List[BookedDay],
                                bookedPeriod: List[BookedDay]): List[BookedDay] =
    currentlyBookedDates.map(
      bd =>
        bookedPeriod.find(_.day == bd.day) match {
          case Some(newBookedDay) => newBookedDay
          case None               => bd
      }
    )
}

case class PeriodBooked(unitId: UnitId, period: List[BookedDay], persistenceSequenceNumber: Long)

class BookedDatesActor(synchronizeBookingActor: ActorRef)
    extends PersistentActor
    with ActorLogging {

  var bookedDatesPerUnit: Map[UnitId, List[BookedDay]] = Map[UnitId, List[BookedDay]]()
  var recoverySequenceNumberForQuery: Long             = 0

  import BookedDatesActor._

  override def receiveCommand: Receive = {
    case EnquiryBookedWithSequenceNumber(persistenceSequenceNumber,
                                         EnquiryBooked(_, _, enquiry, _, _, _)) =>
      val currentlyBookedDates: List[BookedDay] =
        bookedDatesPerUnit.getOrElse(enquiry.unitId, List.empty)

      val bookedPeriod: List[BookedDay] = markNewDates(currentlyBookedDates, enquiry)

      persist(PeriodBooked(enquiry.unitId, bookedPeriod, persistenceSequenceNumber)) { event =>
        bookedDatesPerUnit = bookedDatesPerUnit + (event.unitId -> mergeIntoExistingSchedule(
          currentlyBookedDates,
          bookedPeriod
        ))
        recoverySequenceNumberForQuery = persistenceSequenceNumber
      }
    case GetBookedDates(_, unitId) =>
      sender() ! Good(BookedDays(bookedDatesPerUnit.getOrElse(unitId, List.empty)))
  }

  override def receiveRecover: Receive = {
    case PeriodBooked(unitId, bookedPeriod, persistenceSequenceNumber) =>
      val currentlyBookedDates: List[BookedDay] = bookedDatesPerUnit.getOrElse(unitId, List.empty)
      bookedDatesPerUnit = bookedDatesPerUnit + (unitId -> mergeIntoExistingSchedule(
        currentlyBookedDates,
        bookedPeriod
      ))
      recoverySequenceNumberForQuery = persistenceSequenceNumber
  }

  override def preStart(): Unit =
    synchronizeBookingActor ! StartSync(self,
                                        BookingAggregateActor.persistenceId,
                                        recoverySequenceNumberForQuery)

  override def persistenceId: String = "BookedDatesActor"
}
