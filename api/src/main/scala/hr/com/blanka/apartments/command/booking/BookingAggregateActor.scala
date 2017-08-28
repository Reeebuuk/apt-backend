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
    case e: KnownBookingCommand => (e.enquiryId.id.toString, e)
  }

  def extractShardId: ShardRegion.ExtractShardId = _ => "one"

  val persistenceId = "BookingAggregateActor"
}

class BookingAggregateActor extends PersistentActor with ActorLogging {

  override def receiveCommand: Receive = init

  def init: Receive = {
    case SaveEnquiry(userId, enquiryId, enq, source) =>
      persist(EnquiryReceived(userId, enquiryId, enq, source, LocalDateTime.now())) { e =>
        if (Source.needsApproval(source))
          context become enquiry(enq.unitId, enq.dateFrom, enq.dateTo)
        else
          context become approvedEnquiry(enq.unitId, enq.dateFrom, enq.dateTo)
        sender() ! Good(enquiryId)
      }
    case e =>
      val error = s"Received ${e.toString} in init state"
      log.error(error)
      sender() ! Bad(One(error))
  }

  def enquiry(unitId: UnitId, from: LocalDate, to: LocalDate): Receive = {
    case ApproveEnquiry(userId, enquiryId) =>
      persist(
        EnquiryApproved(userId, enquiryId, LocalDateTime.now(), unitId, from, to)
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
    case DepositPaid(userId, enquiryId, depositAmount, currency) =>
      persist(
        EnquiryBooked(
          userId = userId,
          enquiryId = enquiryId,
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
    case EnquiryReceived(_, enquiryId, e, _, source)
        if enquiryId.id.toString == context.self.path.name =>
      context become enquiry(e.unitId, e.dateFrom, e.dateTo)
    case EnquiryBooked(_, enquiryId, _, _, _, _, _, _)
        if enquiryId.id.toString == context.self.path.name =>
      context become done
  }

  override def persistenceId: String = BookingAggregateActor.persistenceId
}
