package hr.com.blanka.apartments.query

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import hr.com.blanka.apartments.query.booking.{BookingQuery, QueryBookingActor}
import hr.com.blanka.apartments.query.price.{PriceQuery, QueryPriceActor}

import scala.concurrent.duration._
import scala.language.postfixOps

object QueryActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[QueryActor], materializer)
}

class QueryActor(materializer: ActorMaterializer) extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val priceActor = context.actorOf(QueryPriceActor(materializer), "QueryPriceActor")
  val bookingActor = context.actorOf(QueryBookingActor(), "QueryBookingActor")

  override def receive: Receive = {
    case e : PriceQuery => priceActor ? e
    case e : BookingQuery => bookingActor ? e
  }
}
