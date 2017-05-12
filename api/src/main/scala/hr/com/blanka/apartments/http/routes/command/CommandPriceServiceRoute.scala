package hr.com.blanka.apartments.http.routes.command

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import hr.com.blanka.apartments.http.model.{ ErrorResponse, SavePriceRangeRequest }
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.utils.ReadMarshallingSupport
import org.scalactic._

trait CommandPriceServiceRoute extends BaseServiceRoute with ReadMarshallingSupport {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def commandPriceRoute(command: ActorRef): Route = pathPrefix("price") {
    pathEndOrSingleSlash {
      post {
        decodeRequest {
          entity(as[SavePriceRangeRequest]) { savePriceRange =>
            onSuccess(command ? savePriceRange.toCommand) {
              case Good => complete(StatusCodes.OK)
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
