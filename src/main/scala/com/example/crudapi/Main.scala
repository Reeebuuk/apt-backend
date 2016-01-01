package com.example.crudapi

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.example.crudapi.http.BaseService
import com.example.crudapi.price.PriceRangeActor
import com.example.crudapi.utils.{AppConfig, PricingConfig}

import scala.concurrent.ExecutionContext

object Main extends App with AppConfig with BaseService {

  implicit val system = ActorSystem("booking")

  val processor = system.actorOf(PriceRangeActor(PricingConfig(pricingConfig)), "processorActor")
  val view = system.actorOf(PriceRangeActor(PricingConfig(pricingConfig)), "viewActor")

  override protected implicit val executor: ExecutionContext = system.dispatcher
  override protected val log: LoggingAdapter = Logging(system, getClass)
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  Http().bindAndHandle(routes(processor, view), httpInterface, httpPort)
}

