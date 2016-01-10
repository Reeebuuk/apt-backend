package hr.com.blanka.apartments

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import hr.com.blanka.apartments.http.BaseService
import hr.com.blanka.apartments.price.query.QueryPriceRangeActor
import hr.com.blanka.apartments.utils.{AppConfig, PricingConfig}
import kamon.Kamon

object Main extends App with KamonSupport with AppConfig with BaseService with MongoDbConfiguration {

  implicit val system = ActorSystem("booking")

  val command = system.actorOf(QueryPriceRangeActor(PricingConfig(pricingConfig)), "commandActor")
  val query = system.actorOf(QueryPriceRangeActor(PricingConfig(pricingConfig)), "queryActor")

  override protected implicit val executor = system.dispatcher
  override protected val log: LoggingAdapter = Logging(system, getClass)
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(routes(command, query), httpInterface, httpPort)
}

trait KamonSupport {
//  Kamon.start()
//  sys.addShutdownHook(Kamon.shutdown())
}

