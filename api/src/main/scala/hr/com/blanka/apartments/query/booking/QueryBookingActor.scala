package hr.com.blanka.apartments.query.booking

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.stream.ActorMaterializer
import akka.pattern.{ ask, pipe }

import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object QueryBookingActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[QueryBookingActor], materializer)
}

class QueryBookingActor(materializer: ActorMaterializer) extends Actor with ActorLogging {

  implicit val timeout = Timeout(10 seconds)

  val synchronizeBookingActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "synchronizeBookingActor",
    entityProps = SynchronizeBookingActor(materializer),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = SynchronizeBookingActor.extractEntityId,
    extractShardId = SynchronizeBookingActor.extractShardId
  )

  val bookedDatesActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "bookedDatesActor",
    entityProps = BookedDatesActor(synchronizeBookingActor),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = BookedDatesActor.extractEntityId,
    extractShardId = BookedDatesActor.extractShardId
  )

  val unitAvailabilityActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "unitAvailabilityActor",
    entityProps = UnitAvailabilityActor(synchronizeBookingActor),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = UnitAvailabilityActor.extractEntityId,
    extractShardId = UnitAvailabilityActor.extractShardId
  )

  override def receive: Receive = {
    case e: GetAvailableUnits =>
      val msgSender = sender()
      unitAvailabilityActor ? e pipeTo msgSender
    case e: GetBookedDates =>
      val msgSender = sender()
      bookedDatesActor ? e pipeTo msgSender
  }
}
