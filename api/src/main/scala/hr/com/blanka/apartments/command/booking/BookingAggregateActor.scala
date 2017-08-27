package hr.com.blanka.apartments.command.booking

import java.time.{ LocalDate, LocalDateTime }

import akka.actor.{ ActorLogging, Props }
import akka.cluster.sharding.ShardRegion
import akka.persistence.PersistentActor
import hr.com.blanka.apartments.common.ValueClasses.UnitId
import org.scalactic.{ Bad, Good, One }

object BookingAggregateActor {
  def apply() = Props(classOf[BookingAggregateActor])

  def extractEntityId: ShardRegion.ExtractEntityId = {
    case e: KnownBookingCommand => (e.bookingId.id.toString, e)
  }

  def extractShardId: ShardRegion.ExtractShardId = _ => "one"

  val persistenceId = "BookingAggregateActor"
}

class BookingAggregateActor extends PersistentActor with ActorLogging {

  override def receiveCommand: Receive = init

  def init: Receive = {
    case SaveEnquiry(userId, bookingId, enq, source) =>
      persist(EnquiryReceived(userId, bookingId, enq, source, LocalDateTime.now())) { e =>
        if (Source.needsApproval(source))
          context become enquiry(enq.unitId, enq.dateFrom, enq.dateTo)
        else
          context become approvedEnquiry(enq.unitId, enq.dateFrom, enq.dateTo)
        sender() ! Good(bookingId)
      }
    case e =>
      val error = s"Received ${e.toString} in init state"
      log.error(error)
      sender() ! Bad(One(error))
  }

  def enquiry(unitId: UnitId, from: LocalDate, to: LocalDate): Receive = {
    case ApproveEnquiry(userId, bookingId) =>
      persist(
        EnquiryApproved(userId, bookingId, LocalDateTime.now(), unitId, from, to)
      ) { e =>
        context become approvedEnquiry(unitId, from, to)
        sender() ! Good
      }
    case e =>
      val error = s"Received ${e.toString} in enquiry state"
      log.error(error)
      sender() ! Bad(One(error))
  }

  def approvedEnquiry(unitId: UnitId, from: LocalDate, to: LocalDate): Receive = {
    case DepositPaid(userId, bookingId, depositAmount, currency) =>
      persist(
        EnquiryBooked(
          userId = userId,
          bookingId = bookingId,
          timeSaved = LocalDateTime.now(),
          unitId = unitId,
          dateFrom = from,
          dateTo = to,
          depositAmount = depositAmount,
          currency = currency
        )
      ) { e =>
        context become done
        sender() ! Good
      }
    case e =>
      val error = s"Received ${e.toString} in approvedEnquiry state"
      log.error(error)
      sender() ! Bad(One(error))
  }

  def done: Receive = {
    case e =>
      val error = s"Received ${e.toString} in done state"
      log.error(error)
      sender() ! Bad(One(error))
  }

  override def receiveRecover: Receive = {
    case EnquiryReceived(_, bookingId, e, _, source)
        if bookingId.id.toString == context.self.path.name =>
      context become enquiry(e.unitId, e.dateFrom, e.dateTo)
    case EnquiryBooked(_, bookingId, _, _, _, _, _, _)
        if bookingId.id.toString == context.self.path.name =>
      context become done
  }

  override def persistenceId: String = BookingAggregateActor.persistenceId
}
