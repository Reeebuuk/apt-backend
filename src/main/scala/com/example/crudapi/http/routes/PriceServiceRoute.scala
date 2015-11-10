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

final case class PriceForRangeDto(unitId: Int, price: BigDecimal, response: String = "PriceForRangeDto")

final case class ErrorDto(unitId: Int, msg: String, response: String = "ErrorDto")

trait PriceServiceRoute extends BaseServiceRoute with MarshallingSupport {

  implicit val timeout = Timeout(30 seconds)

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

            val result = Await.ready(pricePromise.future, Duration.Inf).value.get

            result match {
              case Success(s) => s match {
                case PriceForRangeCalculated(unitId, price) =>
                  complete(PriceForRangeDto(unitId, price))
                case InvalidRange(unitId) =>
                  complete(ErrorDto(unitId, "ERROR"))
                case _ =>
                  complete(ErrorDto(priceForRange.unitId, "ERROR"))
              }
              case Failure(t) =>
                complete(ErrorDto(priceForRange.unitId, t.getMessage))
            }

          }
        }
      }
    }
  }
}