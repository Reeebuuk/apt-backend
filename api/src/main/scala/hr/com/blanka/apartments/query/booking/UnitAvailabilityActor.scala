package hr.com.blanka.apartments.query.booking

import java.time.LocalDate

import akka.actor.{ Actor, ActorLogging, Props }
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.command.booking.EnquiryBooked
import hr.com.blanka.apartments.common.HardcodedUnits
import hr.com.blanka.apartments.common.ValueClasses.UnitId
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import hr.com.blanka.apartments.utils.DateHelperMethods
import org.scalactic.Good

object UnitAvailabilityActor extends DateHelperMethods {
  def apply() = Props(classOf[UnitAvailabilityActor])

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case PersistenceQueryEvent(_, e: EnquiryBooked) => (e.userId.id.toString, e)
    case e: GetAvailableUnits                       => (e.userId.id.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = _ => "two"

  def getAvailableUnits(from: LocalDate,
                        to: LocalDate,
                        bookedUnitsPerDate: Map[LocalDate, Set[UnitId]]): Set[UnitId] =
    HardcodedUnits.units.keySet.diff(getBookedUnits(from, to, bookedUnitsPerDate))

  def getBookedUnits(from: LocalDate,
                     to: LocalDate,
                     bookedUnitsPerDate: Map[LocalDate, Set[UnitId]]): Set[UnitId] =
    iterateThroughDaysIncludingLast(from, to).flatMap(bookedUnitsPerDate.getOrElse(_, Set())).toSet

  def checkIfUnitIdIsBooked(unitId: UnitId,
                            from: LocalDate,
                            to: LocalDate,
                            bookedUnitsPerDate: Map[LocalDate, Set[UnitId]]): Boolean =
    getBookedUnits(from, to, bookedUnitsPerDate).toList.contains(unitId)

  def update(e: BookedUnit,
             bookedUnitsPerDate: Map[LocalDate, Set[UnitId]]): Map[LocalDate, Set[UnitId]] =
    bookedUnitsPerDate.get(e.date) match {
      case None        => bookedUnitsPerDate + (e.date -> Set(e.unitId))
      case Some(units) => bookedUnitsPerDate + (e.date -> (units + e.unitId))
    }
}

class UnitAvailabilityActor extends Actor with ActorLogging {

  import UnitAvailabilityActor._

  override def receive: Receive = init(Map.empty)

  def init(bookedUnitsPerDate: Map[LocalDate, Set[UnitId]]): Receive = {
    case GetAvailableUnits(_, from, to) =>
      val lala = bookedUnitsPerDate
      sender() ! Good(AvailableUnits(getAvailableUnits(from, to, bookedUnitsPerDate)))

    case e: EnquiryBooked =>
      iterateThroughDaysIncludingLast(e.dateFrom, e.dateTo).foreach(
        date => {
          val state = update(BookedUnit(e.userId, e.unitId, date), bookedUnitsPerDate)
          context become init(state)
        }
      )
  }

}
