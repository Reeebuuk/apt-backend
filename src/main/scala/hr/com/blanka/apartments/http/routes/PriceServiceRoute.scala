package hr.com.blanka.apartments.http.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.price.protocol.{InvalidRange, LookupPriceForRange, PriceForRangeCalculated, PriceQueryResponse}
import hr.com.blanka.apartments.utils.MarshallingSupport
import org.scalactic._

import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.language.postfixOps

final case class CalculatePriceForRangeDto(unitId: Int, from: Long, to: Long)

final case class SavePriceForRange(userId: String, unitId: Int, from: Long, to: Long, price: Int)

final case class PriceForRangeResponse(price: BigDecimal)

final case class ErrorResponse(msg: String)

trait PriceServiceRoute extends BaseServiceRoute with MarshallingSupport {

  implicit val timeout = Timeout(9 seconds)

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def priceRoute(command: ActorRef, query: ActorRef) = pathPrefix("price") {
    path("calculate") {
      post {
        decodeRequest {
          entity(as[CalculatePriceForRangeDto]) { priceForRange: CalculatePriceForRangeDto =>
            val pricePromise = Promise[PriceQueryResponse]()

            query ! LookupPriceForRange(
              "user",
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
      }
    } ~
      pathEndOrSingleSlash {
        post {
          decodeRequest {
            entity(as[SavePriceForRange]) { savePriceForRange =>
              onSuccess(command ? savePriceForRange){
                case Good(_) => complete(StatusCodes.OK, "Saved")
                case Bad(response) => response match {
                  case One(error) => complete(StatusCodes.BadRequest, error.toString)
                  case Many(first, second) => complete(StatusCodes.BadRequest, Seq(first, second).mkString(", "))
                }
                case _ => complete(StatusCodes.BadRequest, "Not the right one :/")
              }
            }
          }
        }
      }
  }
}