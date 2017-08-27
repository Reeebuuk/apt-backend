package hr.com.blanka.apartments.query.booking

import java.time.LocalDate

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.command.booking.{ BookingAggregateActor, EnquiryBooked }
import hr.com.blanka.apartments.common.HardcodedUnits
import hr.com.blanka.apartments.common.ValueClasses.UnitId
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import hr.com.blanka.apartments.utils.DateHelperMethods
import org.scalactic.Good

object UnitAvailabilityActor {
  def apply(parent: ActorRef) = Props(classOf[UnitAvailabilityActor], parent)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e: EnquiryBooked     => (e.userId.id.toString, e)
    case e: GetAvailableUnits => (e.userId.id.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = _ => "two"
}

class UnitAvailabilityActor(parent: ActorRef)
    extends Actor
    with ActorLogging
    with DateHelperMethods {

  var bookedUnitsPerDate: Map[LocalDate, Set[UnitId]] = Map[LocalDate, Set[UnitId]]()
  var persistenceSequenceNumber: Long                 = 0

  override def receive: Receive = {
    case GetAvailableUnits(_, from, to) =>
      sender() ! Good(AvailableUnits(getAvailableUnits(from, to)))

    case PersistenceQueryEvent(sequenceNumber, e: EnquiryBooked) =>
      iterateThroughDaysIncludingLast(e.dateFrom, e.dateTo).foreach(
        date => update(BookedUnit(e.userId, e.unitId, date, sequenceNumber))
      )
  }

  def getAvailableUnits(from: LocalDate, to: LocalDate): Set[UnitId] =
    HardcodedUnits.units.keySet.diff(getBookedUnits(from, to))

  def getBookedUnits(from: LocalDate, to: LocalDate): Set[UnitId] =
    iterateThroughDaysIncludingLast(from, to).flatMap(bookedUnitsPerDate.getOrElse(_, Set())).toSet

  def checkIfUnitIdIsBooked(unitId: UnitId, from: LocalDate, to: LocalDate): Boolean =
    getBookedUnits(from, to).toList.contains(unitId)

  def update(e: BookedUnit): Unit = {
    bookedUnitsPerDate = bookedUnitsPerDate.get(e.date) match {
      case None        => bookedUnitsPerDate + (e.date -> Set(e.unitId))
      case Some(units) => bookedUnitsPerDate + (e.date -> (units + e.unitId))
    }
    persistenceSequenceNumber = e.sequenceNumber
  }

  override def preStart(): Unit = {
    parent ! StartSync(self, BookingAggregateActor.persistenceId, persistenceSequenceNumber)
    super.preStart()
  }

}
