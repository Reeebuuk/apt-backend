package hr.com.blanka.apartments.query.booking

import java.time.{ Duration, LocalDate }

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.ValueClasses.UnitId
import hr.com.blanka.apartments.command.booking.{
  BookingAggregateActor,
  CheckIfPeriodIsAvailable,
  EnquiryBooked
}
import hr.com.blanka.apartments.common.HardcodedUnits
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import org.scalactic.Good

object UnitAvailabilityActor {
  def apply(synchronizeBookingActor: ActorRef) =
    Props(classOf[UnitAvailabilityActor], synchronizeBookingActor)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e: EnquiryBooked            => (e.userId.id.toString, e)
    case e: GetAvailableUnits        => (e.userId.id.toString, e)
    case e: CheckIfPeriodIsAvailable => (e.userId.id.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = _ => "two"
}

class UnitAvailabilityActor(synchronizeBookingActor: ActorRef) extends Actor with ActorLogging {

  var bookedUnitsPerDate: Map[LocalDate, Set[UnitId]] = Map[LocalDate, Set[UnitId]]()
  var persistenceSequenceNumber: Long                 = 0

  override def receive: Receive = {
    case CheckIfPeriodIsAvailable(_, unitId, from, to) =>
      sender() ! checkIfUnitIdIsBooked(unitId, from, to)

    case GetAvailableUnits(_, from, to) =>
      sender() ! Good(AvailableUnits(getAvailableUnits(from, to)))

    case PersistenceQueryEvent(sequenceNumber, event: EnquiryBooked) =>
      iterateThroughDays(event.enquiry.dateFrom, event.enquiry.dateTo).foreach(
        date => update(BookedUnit(event.userId, event.enquiry.unitId, date, sequenceNumber))
      )
  }

  def getAvailableUnits(from: LocalDate, to: LocalDate): Set[UnitId] =
    HardcodedUnits.units.keySet.diff(getBookedUnits(from, to))

  def getBookedUnits(from: LocalDate, to: LocalDate): Set[UnitId] =
    iterateThroughDays(from, to).flatMap(bookedUnitsPerDate.getOrElse(_, Set())).toSet

  def checkIfUnitIdIsBooked(unitId: UnitId, from: LocalDate, to: LocalDate): Boolean =
    getBookedUnits(from, to).toList.contains(unitId)

  def iterateThroughDays(from: LocalDate, to: LocalDate): List[LocalDate] =
    (0l to Duration.between(from.atStartOfDay(), to.atStartOfDay()).toDays)
      .map(from.plusDays)
      .toList

  def update(e: BookedUnit): Unit = {
    bookedUnitsPerDate = bookedUnitsPerDate.get(e.date) match {
      case None        => bookedUnitsPerDate + (e.date -> Set(e.unitId))
      case Some(units) => bookedUnitsPerDate + (e.date -> (units + e.unitId))
    }
    persistenceSequenceNumber = e.sequenceNumber
  }

  override def preStart(): Unit = {
    synchronizeBookingActor ! StartSync(self,
                                        BookingAggregateActor.persistenceId,
                                        persistenceSequenceNumber)
    super.preStart()
  }

}
