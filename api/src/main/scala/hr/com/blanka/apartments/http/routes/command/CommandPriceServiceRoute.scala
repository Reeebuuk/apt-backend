package hr.com.blanka.apartments.http.routes.command

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.http.model.{ ErrorResponse, SavePriceRangeRequest }
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.utils.ReadMarshallingSupport
import org.scalactic._

trait CommandPriceServiceRoute extends BaseServiceRoute with ReadMarshallingSupport {

  import Directives._
  import PlayJsonSupport._

  def commandPriceRoute(command: ActorRef): Route = pathPrefix("price") {
    pathEndOrSingleSlash {
      post {
        decodeRequest {
          extractUser { userId =>
            entity(as[SavePriceRangeRequest]) { savePriceRange =>
              onSuccess(command ? savePriceRange.toCommand(userId)) {
                case Good => complete(StatusCodes.OK)
                case Bad(response) =>
                  response match {
                    case One(error) =>
                      complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
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
}
