package hr.com.blanka.apartments.http.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import hr.com.blanka.apartments.command.price.SavePriceRange
import hr.com.blanka.apartments.query.price.LookupPriceForRange
import hr.com.blanka.apartments.utils.MarshallingSupport
import org.scalactic._

final case class PriceForRangeResponse(price: Double)

trait PriceServiceRoute extends BaseServiceRoute with MarshallingSupport {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def priceRoute(command: ActorRef, query: ActorRef) = pathPrefix("price") {
    pathEndOrSingleSlash {
      post {
        decodeRequest {
          entity(as[SavePriceRange]) { savePriceRange =>
            onSuccess(command ? savePriceRange) {
              case Good => complete(StatusCodes.OK)
              case Bad(response) => response match {
                case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                case Many(first, second) => complete(StatusCodes.BadRequest, ErrorResponse(Seq(first, second).mkString(", ")))
                case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
              }
            }
          }
        }
      }
    } ~
      path("calculate") {
        post {
          decodeRequest {
            entity(as[LookupPriceForRange]) { lookupPriceForRange =>
              onSuccess(query ? lookupPriceForRange) {
                case Good(result) => complete(StatusCodes.OK, PriceForRangeResponse(result.asInstanceOf[Double]))
                case Bad(response) => response match {
                  case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                  case Many(first, second) => complete(StatusCodes.BadRequest, ErrorResponse(Seq(first, second).mkString(", ")))
                  case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                }
              }
            }
          }
        }
      }
  }
}