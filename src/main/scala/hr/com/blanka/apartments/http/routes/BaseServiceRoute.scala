package hr.com.blanka.apartments.http.routes

import akka.event.LoggingAdapter
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

trait BaseServiceRoute {
  protected implicit def executor: ExecutionContext
  protected implicit def materializer: ActorMaterializer
  protected def log: LoggingAdapter
}
