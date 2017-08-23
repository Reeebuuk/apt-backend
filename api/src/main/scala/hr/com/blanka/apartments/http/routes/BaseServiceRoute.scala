package hr.com.blanka.apartments.http.routes

import akka.event.LoggingAdapter
import akka.stream.ActorMaterializer
import hr.com.blanka.apartments.utils.PredefinedTimeout

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

trait BaseServiceRoute extends PredefinedTimeout {
  protected implicit def executor: ExecutionContext
  protected implicit def materializer: ActorMaterializer
  protected def log: LoggingAdapter
}
