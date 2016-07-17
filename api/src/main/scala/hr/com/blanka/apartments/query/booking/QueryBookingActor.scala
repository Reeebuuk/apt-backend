package hr.com.blanka.apartments.query.booking

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.stream.ActorMaterializer
import akka.pattern.{ask, pipe}

import akka.util.Timeout
import hr.com.blanka.apartments.command.price.PriceAggregateActor

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object QueryBookingActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[QueryBookingActor], materializer)
}

class QueryBookingActor(materializer: ActorMaterializer) extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val bookedDatesActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "bookedDatesActor",
    entityProps = BookedDatesActor(),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = BookedDatesActor.extractEntityId,
    extractShardId = BookedDatesActor.extractShardId)

  val unitAvailabilityActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "unitAvailabilityActor",
    entityProps = UnitAvailabilityActor(),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = UnitAvailabilityActor.extractEntityId,
    extractShardId = UnitAvailabilityActor.extractShardId)

  val synchronizeBookingActor = context.actorOf(SynchronizeBookingActor(materializer), "synchronizeBookingActor")

  override def receive: Receive = {
    case e: StartSync => synchronizeBookingActor ! e
    case e: GetAvailableApartments =>
      val msgSender = sender()
      unitAvailabilityActor ? e pipeTo msgSender
  }
}
