package hr.com.blanka.apartments.query.booking

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import hr.com.blanka.apartments.utils.PredefinedTimeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object QueryBookingActor {
  def apply() = Props(classOf[QueryBookingActor])
}

class QueryBookingActor() extends Actor with ActorLogging with PredefinedTimeout {

  val bookedDatesActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "BookedDatesActor",
    entityProps = BookedDatesActor(self),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = BookedDatesActor.extractEntityId,
    extractShardId = BookedDatesActor.extractShardId
  )

  val unitAvailabilityActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "UnitAvailabilityActor",
    entityProps = UnitAvailabilityActor(self),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = UnitAvailabilityActor.extractEntityId,
    extractShardId = UnitAvailabilityActor.extractShardId
  )

  val allEnquiriesActor: ActorRef =
    context.actorOf(AllEnquiriesActor(), "AllEnquiriesActor")

  override def receive: Receive = {
    case e: GetAvailableUnits =>
      val msgSender = sender()
      unitAvailabilityActor ? e pipeTo msgSender
    case e: GetBookedDates =>
      val msgSender = sender()
      bookedDatesActor ? e pipeTo msgSender
    case e @ (_: GetAllBookedEnquiries | _: GetAllUnapprovedEnquiries |
        _: GetAllApprovedEnquiries) =>
      val msgSender = sender()
      allEnquiriesActor ? e pipeTo msgSender
    case e: StartSync =>
      context.parent ! e
  }
}
