package hr.com.blanka.apartments

import akka.actor.ActorSystem
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import hr.com.blanka.apartments.http.BaseService
import hr.com.blanka.apartments.price.{DailyPriceAggregateActor, QueryPriceRangeActor, CommandPriceRangeActor}
import hr.com.blanka.apartments.utils.AppConfig
import kamon.Kamon

object Main extends App with KamonSupport with AppConfig with BaseService {

  implicit val system = ActorSystem("booking")

  val command = system.actorOf(CommandPriceRangeActor(), "commandActor")
  val query = system.actorOf(QueryPriceRangeActor(), "queryActor")

  override protected implicit val executor = system.dispatcher
  override protected val log: LoggingAdapter = Logging(system, getClass)
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  ClusterSharding(system).start(
    typeName = DailyPriceAggregateActor.shardName,
    entityProps = DailyPriceAggregateActor(),
    settings = ClusterShardingSettings(system),
    extractEntityId = DailyPriceAggregateActor.idExtractor,
    extractShardId = DailyPriceAggregateActor.shardResolver)

  Http().bindAndHandle(routes(command, query), httpInterface, httpPort)
}

trait KamonSupport {
//  Kamon.start()
//  sys.addShutdownHook(Kamon.shutdown())
}

