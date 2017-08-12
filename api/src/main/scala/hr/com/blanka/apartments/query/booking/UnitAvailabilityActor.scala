package hr.com.blanka.apartments.query.booking

import java.time.temporal.TemporalUnit
import java.time.{ Duration, LocalDate }

import akka.actor.{ ActorLogging, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.command.booking.{
  BookingAggregateActor,
  CheckIfPeriodIsAvailable,
  EnquiryBooked
}
import org.scalactic.{ Bad, Good }

object UnitAvailabilityActor {
  def apply(synchronizeBookingActor: ActorRef) =
    Props(classOf[UnitAvailabilityActor], synchronizeBookingActor)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e: EnquiryBooked            => (e.userId.toString, e)
    case e: GetAvailableApartments   => (e.userId.toString, e)
    case e: CheckIfPeriodIsAvailable => (e.userId.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case _ => "two"
  }
}

class UnitAvailabilityActor(synchronizeBookingActor: ActorRef)
    extends PersistentActor
    with ActorLogging {

  var bookedUnitsPerDate = Map[LocalDate, Set[Int]]()
  var sequenceNmbr: Long = 0

  override def receiveCommand: Receive = {
    case CheckIfPeriodIsAvailable(_, unitId, from, to) =>
      sender() ! checkIfUnitIdIsBooked(unitId, from, to)

    case GetAvailableApartments(_, from, to) =>
      sender() ! Good(AvailableApartments(getAvailableApartments(from, to)))

    case EnquiryBookedWithSeqNmr(nmbr, EnquiryBooked(userId, bookingId, enquiry, _, _, _)) =>
      iterateThroughDays(enquiry.dateFrom, enquiry.dateTo).foreach(
        date =>
          persist(BookedUnit(userId, enquiry.unitId, date, nmbr)) { event =>
            update(event)
        }
      )
  }

  //currently hardcoded for 3 apartments
  def getAvailableApartments(from: LocalDate, to: LocalDate) =
    Set(1, 2, 3).diff(getBookedApartments(from, to))

  def getBookedApartments(from: LocalDate, to: LocalDate): Set[Int] =
    iterateThroughDays(from, to).flatMap(bookedUnitsPerDate.getOrElse(_, Set())).toSet

  def checkIfUnitIdIsBooked(unitId: Int, from: LocalDate, to: LocalDate) =
    getBookedApartments(from, to).toList.contains(unitId) match {
      case true  => Bad
      case false => Good
    }

  def iterateThroughDays(from: LocalDate, to: LocalDate): List[LocalDate] =
    (0l to Duration.between(from.atStartOfDay(), to.atStartOfDay()).toDays)
      .map(from.plusDays)
      .toList

  def update(e: BookedUnit): Unit = {
    bookedUnitsPerDate = bookedUnitsPerDate.get(e.date) match {
      case None        => bookedUnitsPerDate + (e.date -> Set(e.unitId))
      case Some(units) => bookedUnitsPerDate + (e.date -> (units + e.unitId))
    }
    sequenceNmbr = e.sequenceNmbr
  }

  override def preStart() =
    synchronizeBookingActor ! StartSync(self, BookingAggregateActor.persistenceId, sequenceNmbr)

  override def receiveRecover: Receive = {
    case e: BookedUnit => update(e)
  }

  override def persistenceId: String = "UnitAvailabilityActor"
}
