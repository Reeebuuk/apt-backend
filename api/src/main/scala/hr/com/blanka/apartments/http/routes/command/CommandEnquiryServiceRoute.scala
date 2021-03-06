package hr.com.blanka.apartments.http.routes.command

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.command.booking.ApproveEnquiry
import hr.com.blanka.apartments.common.ValueClasses.EnquiryId
import hr.com.blanka.apartments.http.model._
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.utils.ReadMarshallingSupport
import org.scalactic._

trait CommandEnquiryServiceRoute extends BaseServiceRoute with ReadMarshallingSupport {

  import Directives._
  import PlayJsonSupport._

  def commandEnquiryRoute(command: ActorRef): Route = pathPrefix("enquiry") {
    pathEndOrSingleSlash {
      post {
        decodeRequest {
          extractUser { userId =>
            entity(as[EnquiryRequest]) { booking =>
              onSuccess(command ? booking.toCommand(userId)) {
                case Good(enquiryId) =>
                  complete(StatusCodes.OK, NewEnquiryResponse(enquiryId match {
                    case x: EnquiryId => x.id
                    case _            => 0l
                  }))
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
    } ~
    path("depositPaid") {
      post {
        decodeRequest {
          extractUser { userId =>
            entity(as[DepositPaidRequest]) { depositPaid =>
              onSuccess(command ? depositPaid.toCommand(userId)) {
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
    } ~
    pathPrefix(LongNumber) { enquiryId =>
      path("authorize") {
        put {
          extractUser { userId =>
            onSuccess(command ? ApproveEnquiry(userId, EnquiryId(enquiryId))) {
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
