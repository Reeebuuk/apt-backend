package hr.com.blanka.apartments.http.routes.command

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.http.model.{ ContactRequest, ErrorResponse }
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.utils.ReadMarshallingSupport
import org.scalactic._

trait CommandContactServiceRoute extends BaseServiceRoute with ReadMarshallingSupport {

  import Directives._
  import PlayJsonSupport._

  def commandContactRoute(command: ActorRef): Route = pathPrefix("contact") {
    pathEndOrSingleSlash {
      post {
        decodeRequest {
          entity(as[ContactRequest]) { contactRequest =>
            onSuccess(command ? contactRequest.toCommand) {
              case Good => complete(StatusCodes.OK)
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
