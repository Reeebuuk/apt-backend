package hr.com.blanka.apartments.query.booking

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.ShardRegion
import hr.com.blanka.apartments.ValueClasses.UnitId
import hr.com.blanka.apartments.command.booking.{ BookingAggregateActor, EnquiryBooked }
import hr.com.blanka.apartments.common.Enquiry
import hr.com.blanka.apartments.query.PersistenceQueryEvent
import hr.com.blanka.apartments.utils.HelperMethods
import org.scalactic.Good

object BookedDatesActor {
  def apply(parent: ActorRef) = Props(classOf[BookedDatesActor], parent)

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case e: GetBookedDates => (e.userId.id.toString, e)
  }

  val extractShardId: ShardRegion.ExtractShardId = _ => "two"

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
    currentlyBookedDates.union(bookedPeriod).groupBy(t => (t.day, t)).map(c => c._2.head).toList

}

class BookedDatesActor(parent: ActorRef) extends Actor with ActorLogging {

  var bookedDatesPerUnit: Map[UnitId, List[BookedDay]] = Map[UnitId, List[BookedDay]]()

  import BookedDatesActor._

  override def receive: Receive = {
    case PersistenceQueryEvent(sequenceNumber, e: EnquiryBooked) =>
      val currentlyBookedDates: List[BookedDay] =
        bookedDatesPerUnit.getOrElse(e.enquiry.unitId, List.empty)

      val bookedPeriod: List[BookedDay] = markNewDates(currentlyBookedDates, e.enquiry)
      bookedDatesPerUnit = bookedDatesPerUnit + (e.enquiry.unitId -> mergeIntoExistingSchedule(
        currentlyBookedDates,
        bookedPeriod
      ))
    case GetBookedDates(_, unitId) =>
      sender() ! Good(BookedDays(bookedDatesPerUnit.getOrElse(unitId, List.empty)))
  }

  override def preStart(): Unit = {
    parent ! StartSync(self, BookingAggregateActor.persistenceId, 0)
    super.preStart()
  }

}
