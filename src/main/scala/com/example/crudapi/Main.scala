package com.example.crudapi

import akka.actor.ActorSystem
import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.example.crudapi.http.HttpService
import com.example.crudapi.utils.AppConfig

import scala.concurrent.ExecutionContext

object Main extends App with AppConfig with HttpService {
  private implicit val system = ActorSystem()

  override protected implicit val executor: ExecutionContext = system.dispatcher
  override protected val log: LoggingAdapter = Logging(system, getClass)
  override protected implicit val materializer: ActorMaterializer = ActorMaterializer()

//  Http().bindAndHandle(routes, httpInterface, httpPort)
}

