package hr.com.blanka.apartments.query.booking

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object QueryBookingActor {
  def apply(commandSideReaderActor: ActorRef) =
    Props(classOf[QueryBookingActor], commandSideReaderActor)
}

class QueryBookingActor(commandSideReaderActor: ActorRef) extends Actor with ActorLogging {

  implicit val timeout = Timeout(10 seconds)

  val bookedDatesActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "bookedDatesActor",
    entityProps = BookedDatesActor(commandSideReaderActor),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = BookedDatesActor.extractEntityId,
    extractShardId = BookedDatesActor.extractShardId
  )

  val unitAvailabilityActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "unitAvailabilityActor",
    entityProps = UnitAvailabilityActor(commandSideReaderActor),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = UnitAvailabilityActor.extractEntityId,
    extractShardId = UnitAvailabilityActor.extractShardId
  )

  val allBookingsActor: ActorRef =
    context.actorOf(AllBookingsActor(commandSideReaderActor), "AllBookingsActor")

  override def receive: Receive = {
    case e: GetAvailableUnits =>
      val msgSender = sender()
      unitAvailabilityActor ? e pipeTo msgSender
    case e: GetBookedDates =>
      val msgSender = sender()
      bookedDatesActor ? e pipeTo msgSender
    case e: GetAllBookings =>
      val msgSender = sender()
      allBookingsActor ? e pipeTo msgSender
  }
}
