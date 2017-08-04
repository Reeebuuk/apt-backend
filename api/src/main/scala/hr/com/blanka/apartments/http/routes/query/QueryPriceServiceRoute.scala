package hr.com.blanka.apartments.http.routes.query

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import hr.com.blanka.apartments.http.model.{ ErrorResponse, LookupPriceForRangeRequest, PriceForRangeResponse }
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.query.price.LookupAllPrices
import hr.com.blanka.apartments.utils.{ ReadMarshallingSupport, WriteMarshallingSupport }
import org.scalactic._

trait QueryPriceServiceRoute extends BaseServiceRoute with WriteMarshallingSupport with ReadMarshallingSupport {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def queryPriceRoute(query: ActorRef): Route = pathPrefix("price") {
    pathEndOrSingleSlash {
      parameter("unitId".as[Int]) { unitId =>
        onSuccess(query ? LookupAllPrices("user", unitId)) {
          case Good(result) => complete(StatusCodes.OK, PriceForRangeResponse(result.asInstanceOf[BigDecimal]))
          case Bad(response) =>
            response match {
              case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
              case Many(first, second) =>
                complete(StatusCodes.BadRequest, ErrorResponse(Seq(first, second).mkString(", ")))
              case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
            }
        }
      }
    } ~
      path("calculate") {
        post {
          decodeRequest {
            entity(as[LookupPriceForRangeRequest]) { lookupPriceForRange =>
              onSuccess(query ? lookupPriceForRange) {
                case Good(result) => complete(StatusCodes.OK, PriceForRangeResponse(result.asInstanceOf[BigDecimal]))
                case Bad(response) =>
                  response match {
                    case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                    case Many(first, second) =>
                      complete(StatusCodes.BadRequest, ErrorResponse(Seq(first, second).mkString(", ")))
                    case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                  }
              }
            }
          }
        }
      }
  }
}
