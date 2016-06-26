package hr.com.blanka.apartments.query

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import hr.com.blanka.apartments.query.booking.{BookingQuery, QueryBookingActor}
import hr.com.blanka.apartments.query.price.{PriceQuery, QueryPriceActor}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps

object QueryActor {
  def apply() = Props(classOf[QueryActor])
}

class QueryActor extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val priceActor = context.actorOf(QueryPriceActor(), "QueryPriceActor")
  val bookingActor = context.actorOf(QueryBookingActor(), "QueryBookingActor")

  override def receive: Receive = {
    case e : PriceQuery => priceActor ? e
    case e : BookingQuery => bookingActor ? e
  }
}
