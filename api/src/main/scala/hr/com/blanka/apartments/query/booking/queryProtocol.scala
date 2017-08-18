package hr.com.blanka.apartments.query.booking

import akka.actor.ActorRef
import java.time.LocalDate

import hr.com.blanka.apartments.ValueClasses.{ UnitId, UserId }

sealed trait BookingQuery

case class GetBookedDates(userId: UserId, unitId: UnitId)                    extends BookingQuery
case class GetAvailableUnits(userId: UserId, from: LocalDate, to: LocalDate) extends BookingQuery

sealed trait BookingQueryResponse

case class BookedDays(bookedDays: List[BookedDay]) extends BookingQueryResponse
case class AvailableUnits(unitIds: Set[UnitId])    extends BookingQueryResponse

case class BookedUnit(userId: UserId, unitId: UnitId, date: LocalDate, sequenceNmbr: Long)

case class BookedDay(day: LocalDate, firstDay: Boolean, lastDay: Boolean)
case class StartSync(actor: ActorRef, persistenceId: String, initialIndex: Long)
