package hr.com.blanka.apartments.query

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.pattern.{ ask, pipe }
import akka.stream.ActorMaterializer
import akka.util.Timeout
import hr.com.blanka.apartments.query.booking.{ BookingQuery, QueryBookingActor }
import hr.com.blanka.apartments.query.price.{ PriceQuery, QueryPriceActor }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object QueryActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[QueryActor], materializer)
}

class QueryActor(materializer: ActorMaterializer) extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val priceActor: ActorRef = context.actorOf(QueryPriceActor(materializer), "QueryPriceActor")
  val bookingActor: ActorRef = context.actorOf(QueryBookingActor(materializer), "QueryBookingActor")

  val queryProjectionSupervisor: ActorRef = context.actorOf(
    QueryProjectionSupervisor(materializer),
    "QueryProjectionSupervisor"
  )

  override def receive: Receive = {
    case e: PriceQuery =>
      val msgSender = sender()
      priceActor ? e pipeTo msgSender
    case e: BookingQuery =>
      val msgSender = sender()
      bookingActor ? e pipeTo msgSender
  }
}
