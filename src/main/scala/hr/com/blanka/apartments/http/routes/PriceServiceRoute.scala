package hr.com.blanka.apartments.http.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import hr.com.blanka.apartments.price.PriceCommandQueryProtocol._
import hr.com.blanka.apartments.utils.MarshallingSupport

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

            onSuccess(pricePromise.future) {
              case PriceForRangeCalculated(price) => complete(PriceForRangeDto(price))
              case InvalidRange => complete(StatusCodes.Conflict, "InvalidRange")
              case _ => complete(StatusCodes.BadRequest, "UnknownError")
            }

          }
        }
      }
    }
  }
}