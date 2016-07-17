package hr.com.blanka.apartments.query.booking

import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.command.booking.{BookingAggregateActor, EnquiryBooked, EnquirySaved}
import org.joda.time.{Days, LocalDate, Period, YearMonth}

object UnitAvailabilityActor {
  def apply() = Props(classOf[UnitAvailabilityActor])

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e : EnquiryBooked => (e.userId.toString, e)
    case e : GetAvailableApartments => (e.userId.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case _ => "one"
  }
}

class UnitAvailabilityActor extends PersistentActor with ActorLogging {

  var bookedUnitsPerDate = Map[LocalDate, Set[Int]]()
  var sequenceNmbr: Long = 0

  override def receiveCommand: Receive = {
    case GetAvailableApartments(_, from, to) =>
      val bookedUnitIds = iterateThroughDays(from, to).flatMap(bookedUnitsPerDate.getOrElse(_, Set())).toSet
      sender() ! AvailableApartments(bookedUnitIds)

    case EnquiryBookedWithSeqNmr(nmbr, EnquiryBooked(userId, bookingId, enquiry, _  )) =>
      iterateThroughDays(enquiry.dateFrom, enquiry.dateTo).foreach( date =>
        persist(BookedUnit(userId, enquiry.unitId, date, nmbr)) { event =>
          update(event)
        }
      )
  }

  def iterateThroughDays(from: Long, to: Long): List[LocalDate] = {
    val fromDate = new LocalDate(from)
    val toDate = new LocalDate(to)

    (0 to Days.daysBetween(fromDate, toDate).getDays).map(fromDate.plusDays).toList
  }

  def update(e: BookedUnit) : Unit = {
    bookedUnitsPerDate.get(e.date) match {
      case None => bookedUnitsPerDate + (e.date -> e.unitId)
      case Some(units) => bookedUnitsPerDate + (e.date -> (units + e.unitId))
    }
    sequenceNmbr = e.sequenceNmbr
  }

  override def preStart() = {
    context.parent ! StartSync(self, BookingAggregateActor.persistenceId, sequenceNmbr)
  }

  override def receiveRecover: Receive = {
    case e: BookedUnit => update(e)
  }

  override def persistenceId: String = "UnitAvailabilityActor"
}
