package hr.com.blanka.apartments.query.price

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import hr.com.blanka.apartments.command.price.DailyPriceSaved
import hr.com.blanka.apartments.query.booking.StartSync

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object QueryPriceActor {
  def apply() = Props(classOf[QueryPriceActor])
}

class QueryPriceActor extends Actor with ActorLogging {

  implicit val timeout: Timeout = Timeout(10 seconds)

  val dailyPriceAggregateActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "DailyPriceAggregateActor",
    entityProps = DailyPriceAggregateActor(),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = DailyPriceAggregateActor.extractEntityId,
    extractShardId = DailyPriceAggregateActor.extractShardId
  )

  val queryPriceRangeActor: ActorRef =
    context.actorOf(QueryPriceRangeActor(dailyPriceAggregateActor), "QueryPriceRangeActor")

  val pricingLegacyActor: ActorRef =
    context.actorOf(PricingLegacyActor(), "PricingLegacyActor")

  override def receive: Receive = {
    case e: DailyPriceSaved =>
      val msgSender = sender()
      dailyPriceAggregateActor ? e pipeTo msgSender
    case e: LookupPriceForRange =>
      val msgSender = sender()
      queryPriceRangeActor ? e pipeTo msgSender
    case e: LookupAllPrices =>
      val msgSender = sender()
      queryPriceRangeActor ? e pipeTo msgSender
    case e: LegacyLookupAllPrices =>
      val msgSender = sender()
      pricingLegacyActor ? e pipeTo msgSender
    case e: StartSync =>
      context.parent ! e
  }
}
