package hr.com.blanka.apartments.http.routes.query

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.common.ValueClasses.UserId
import hr.com.blanka.apartments.http.model._
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.query.price.{
  LegacyLookupAllPrices,
  PriceForRangeCalculated,
  PricePerPeriod
}
import hr.com.blanka.apartments.utils.{ ReadMarshallingSupport, WriteMarshallingSupport }
import org.scalactic._

trait QueryPriceServiceRoute
    extends BaseServiceRoute
    with WriteMarshallingSupport
    with ReadMarshallingSupport {

  import Directives._
  import PlayJsonSupport._

  def queryPriceRoute(query: ActorRef): Route = pathPrefix("price") {
    pathEndOrSingleSlash {
      onSuccess(query ? LegacyLookupAllPrices(UserId("user"))) {
        case Good(result) =>
          complete(StatusCodes.OK,
                   PricePerPeriodsResponse.remap(result.asInstanceOf[List[PricePerPeriod]]))
        case Bad(response) =>
          response match {
            case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
            case Many(first, second) =>
              complete(StatusCodes.BadRequest, ErrorResponse(Seq(first, second).mkString(", ")))
            case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
          }
      }
    } ~
    path("calculate") {
      post {
        decodeRequest {
          entity(as[LookupPriceForRangeRequest]) { lookupPriceForRange =>
            onSuccess(query ? lookupPriceForRange.toQuery) {
              case Good(PriceForRangeCalculated(_, price)) =>
                complete(StatusCodes.OK, PriceForRangeResponse(price))
              case Bad(response) =>
                response match {
                  case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                  case Many(first, second) =>
                    complete(StatusCodes.BadRequest,
                             ErrorResponse(Seq(first, second).mkString(", ")))
                  case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                }
            }
          }
        }
      }
    }
  }
}
