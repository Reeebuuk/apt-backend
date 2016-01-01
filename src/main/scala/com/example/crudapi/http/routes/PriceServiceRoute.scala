package com.example.crudapi.http.routes

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import com.example.crudapi.price.PriceCommandQueryProtocol._
import com.example.crudapi.utils.MarshallingSupport

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps
import scala.util.{Failure, Success}

final case class CalculatePriceForRangeDto(unitId: Int, from: Long, to: Long)

final case class PriceForRangeDto(price: BigDecimal, response: String = "PriceForRangeDto")

final case class ErrorDto(msg: String, response: String = "ErrorDto")

trait PriceServiceRoute extends BaseServiceRoute with MarshallingSupport {

  implicit val timeout = Timeout(2 seconds)

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def customersRoute(command: ActorRef, query: ActorRef) = pathPrefix("price") {
    post {
      path("calculate") {
        decodeRequest {
          entity(as[CalculatePriceForRangeDto]) { priceForRange: CalculatePriceForRangeDto =>
            val pricePromise = Promise[PriceQueryResponse]()

            query ! CalculatePriceForRange(
              priceForRange.unitId,
              priceForRange.from,
              priceForRange.to,
              pricePromise
            )

            //Would like to get rid of this and use bindAndHandleAsync in main but don't know how :P
            val result = Await.ready(pricePromise.future, 2 seconds).value.get

            result match {
              case Success(s) => s match {
                case PriceForRangeCalculated(price) =>
                  complete(PriceForRangeDto(price))
                case InvalidRange =>
                  complete(ErrorDto("InvalidRange"))
                case _ =>
                  complete(ErrorDto("UnknownError"))
              }
              case Failure(t) =>
                complete(ErrorDto(t.getMessage))
            }

          }
        }
      }
    }
  }
}