package hr.com.blanka.apartments.query.booking

import akka.actor.{Actor, ActorLogging, Props}
import akka.stream.ActorMaterializer

object QueryBookingActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[QueryBookingActor], materializer)
}

class QueryBookingActor(materializer: ActorMaterializer) extends Actor with ActorLogging {

  val bookedDatesActor = context.actorOf(BookedDatesActor(materializer), "BookedDatesActor")
  val unitAvailabilityActor = context.actorOf(UnitAvailabilityActor(), "unitAvailabilityActor")

  override def receive: Receive = {
    case _ =>
  }
}
