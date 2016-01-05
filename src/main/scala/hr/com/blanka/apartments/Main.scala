package hr.com.blanka.apartments

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import hr.com.blanka.apartments.http.BaseService
import hr.com.blanka.apartments.price.PriceRangeActor
import hr.com.blanka.apartments.utils.{AppConfig, PricingConfig}
import kamon.Kamon

object Main extends App with AppConfig with BaseService with MongoDbConfiguration {

//  Kamon.start()

  implicit val system = ActorSystem("booking")

  val processor = system.actorOf(PriceRangeActor(PricingConfig(pricingConfig)), "processorActor")
  val view = system.actorOf(PriceRangeActor(PricingConfig(pricingConfig)), "viewActor")

  override protected implicit val executor = system.dispatcher
  override protected val log: LoggingAdapter = Logging(system, getClass)
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(routes(processor, view), httpInterface, httpPort)

//  Kamon.shutdown()
}

