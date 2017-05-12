package hr.com.blanka.apartments.query.booking

import akka.actor.ActorRef
import org.joda.time.LocalDate

sealed trait BookingQuery

case class GetBookedDates(userId: String, unitId: Int) extends BookingQuery
case class GetAvailableApartments(userId: String, from: LocalDate, to: LocalDate) extends BookingQuery

sealed trait BookingQueryResponse

case class BookedDays(bookedDays: List[BookedDay]) extends BookingQueryResponse
case class AvailableApartments(apartmentIds: Set[Int]) extends BookingQueryResponse

case class BookedUnit(userId: String, unitId: Int, date: LocalDate, sequenceNmbr: Long)

case class EnquiryBookedWithSeqNmr(seqNmr: Long, event: Any)

case class BookedDay(day: LocalDate, firstDay: Boolean, lastDay: Boolean)
case class StartSync(actor: ActorRef, persistenceId: String, initialIndex: Long)