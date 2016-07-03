package hr.com.blanka.apartments.query.price

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.{ask, pipe}
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object QueryPriceActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[QueryPriceActor], materializer)
}

class QueryPriceActor(materializer: ActorMaterializer) extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val dailyPriceAggregateActor = context.actorOf(DailyPriceAggregateActor(materializer), "DailyPriceAggregateActor")
  val queryPriceRangeActor = context.actorOf(QueryPriceRangeActor(dailyPriceAggregateActor), "QueryPriceRangeActor")

  override def receive: Receive = {
    case e: LookupPriceForRange =>
      val msgSender = sender()
      queryPriceRangeActor ? e pipeTo msgSender
  }
}
