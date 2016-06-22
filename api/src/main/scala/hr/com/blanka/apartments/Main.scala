package hr.com.blanka.apartments

import akka.actor.{Props, ActorSystem}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import hr.com.blanka.apartments.http.BaseService
import hr.com.blanka.apartments.price.{DailyPriceAggregateActor, CommandPriceRangeActor, QueryPriceRangeActor}
import hr.com.blanka.apartments.utils.AppConfig

object Main extends App with AppConfig with BaseService {

  implicit val system: ActorSystem = ActorSystem("booking")

  override protected implicit val executor = system.dispatcher
  override protected val log: LoggingAdapter = Logging(system, getClass)
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(routes, httpInterface, httpPort)
}
