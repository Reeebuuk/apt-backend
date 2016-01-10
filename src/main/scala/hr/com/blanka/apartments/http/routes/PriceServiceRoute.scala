package hr.com.blanka.apartments.http.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import akka.pattern.ask
import hr.com.blanka.apartments.price.PriceQueryProtocol.{LookupPriceForRange, InvalidRange, PriceForRangeCalculated, PriceQueryResponse}
import hr.com.blanka.apartments.utils.MarshallingSupport
import org.scalactic.{Bad, Good}

import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.language.postfixOps

final case class CalculatePriceForRangeDto(unitId: Int, from: Long, to: Long)

final case class SavePriceForRangeDto(unitId: Int, from: Long, to: Long, price: Int)


final case class PriceForRangeResponse(price: BigDecimal)

final case class ErrorResponse(msg: String)

trait PriceServiceRoute extends BaseServiceRoute with MarshallingSupport {

  implicit val timeout = Timeout(2 seconds)

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def priceRoute(command: ActorRef, query: ActorRef) = pathPrefix("price") {
    post {
      path("calculate") {
        decodeRequest {
          entity(as[CalculatePriceForRangeDto]) { priceForRange: CalculatePriceForRangeDto =>
            val pricePromise = Promise[PriceQueryResponse]()

            query ! LookupPriceForRange(
              priceForRange.unitId,
              priceForRange.from,
              priceForRange.to,
              pricePromise
            )

            onSuccess(pricePromise.future) {
              case PriceForRangeCalculated(price) => complete(PriceForRangeResponse(price))
              case InvalidRange => complete(StatusCodes.Conflict, "InvalidRange")
              case _ => complete(StatusCodes.BadRequest, "UnknownError")
            }

          }
        }
      } ~
        pathEndOrSingleSlash {
          decodeRequest {
            entity(as[SavePriceForRangeDto]) { savePriceForRange =>
              onSuccess(command ? savePriceForRange) {
                case Good(_) => complete(StatusCodes.BadRequest, "Saved")
                case Bad(response) => response match {
                  case _ => complete(StatusCodes.BadRequest, "UnknownError")
                }
              }
            }
          }
        }
    }
  }
}