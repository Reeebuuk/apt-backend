package hr.com.blanka.apartments.query.price

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object QueryPriceActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[QueryPriceActor], materializer)
}

class QueryPriceActor(implicit materializer: ActorMaterializer) extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val dailyPriceAggregateActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "DailyPriceAggregateActor",
    entityProps = DailyPriceAggregateActor(),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = DailyPriceAggregateActor.extractEntityId,
    extractShardId = DailyPriceAggregateActor.extractShardId
  )

  val queryPriceRangeActor: ActorRef =
    context.actorOf(QueryPriceRangeActor(dailyPriceAggregateActor), "QueryPriceRangeActor")

  override def receive: Receive = {
    case e: LookupPriceForRange =>
      val msgSender = sender()
      queryPriceRangeActor ? e pipeTo msgSender
    case e: LookupAllPrices =>
      val msgSender = sender()
      queryPriceRangeActor ? e pipeTo msgSender
  }
}
