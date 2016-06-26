package hr.com.blanka.apartments.http.routes

import akka.event.LoggingAdapter
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

trait BaseServiceRoute {
  protected implicit def executor: ExecutionContext
  protected implicit def materializer: ActorMaterializer
  protected def log: LoggingAdapter

  implicit val timeout = Timeout(3 seconds)

}
