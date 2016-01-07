package hr.com.blanka.apartments.http.routes

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import hr.com.blanka.apartments.price.PriceCommandProtocol.PriceForRangeSaved
import hr.com.blanka.apartments.price.PriceQueryProtocol._
import hr.com.blanka.apartments.utils.MarshallingSupport

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}
import scala.language.postfixOps
import scala.util.{Failure, Success}
import akka.pattern.ask

final case class CalculatePriceForRangeDto(unitId: Int, from: Long, to: Long)

final case class SavePriceForRangeDto(unitId: Int, from: Long, to: Long, price: Int)

sealed trait Response

final case class PriceForRangeResponse(price: BigDecimal) extends Response

final case class SavePriceForRangeResponse(response: String) extends Response

final case class ErrorResponse(response: String) extends Response

trait PriceServiceRoute extends BaseServiceRoute with MarshallingSupport {

  implicit val timeout = Timeout(2 seconds)

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def priceRoute(command: ActorRef, query: ActorRef) = pathPrefix("price") {
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
                  complete(PriceForRangeResponse(price))
                case InvalidRange =>
                  complete(ErrorResponse("InvalidRange"))
                case _ =>
                  complete(ErrorResponse("UnknownError"))
              }
              case Failure(t) =>
                complete(ErrorResponse(t.getMessage))
            }

          }
        }
      }
      pathEndOrSingleSlash {
        decodeRequest {
          entity(as[SavePriceForRangeDto]) { savePriceForRange =>
            (command ? savePriceForRange).mapTo[Response].onSuccess(
            {
              case _ =>
                complete(SavePriceForRangeResponse("Saved"))
            }
            )
          }
        }
      }
    }
  }
}