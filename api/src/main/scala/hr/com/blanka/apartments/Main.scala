package hr.com.blanka.apartments

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import hr.com.blanka.apartments.command.CommandActor
import hr.com.blanka.apartments.http.BaseService
import hr.com.blanka.apartments.query.QueryActor
import hr.com.blanka.apartments.utils.AppConfig

object Main extends App with AppConfig with BaseService {

  implicit val system: ActorSystem = ActorSystem("BookingSystem")

  override protected implicit val executor = system.dispatcher
  override protected val log: LoggingAdapter = Logging(system, getClass)
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  val command = system.actorOf(CommandActor(), "CommandActor")
  val query = system.actorOf(QueryActor(materializer), "QueryActor")

  Http().bindAndHandle(routes(command, query), httpInterface, httpPort)
}
