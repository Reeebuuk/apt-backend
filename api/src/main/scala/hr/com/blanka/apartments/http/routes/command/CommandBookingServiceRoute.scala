package hr.com.blanka.apartments.http.routes.command

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.ValueClasses.BookingId
import hr.com.blanka.apartments.http.model.{
  DepositPaidRequest,
  EnquiryReceivedRequest,
  ErrorResponse,
  NewEnquiryResponse
}
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.utils.ReadMarshallingSupport
import org.scalactic._

trait CommandBookingServiceRoute extends BaseServiceRoute with ReadMarshallingSupport {

  import Directives._
  import PlayJsonSupport._

  def commandBookingRoute(command: ActorRef): Route = pathPrefix("booking") {
    pathEndOrSingleSlash {
      post {
        decodeRequest {
          entity(as[EnquiryReceivedRequest]) { booking =>
            onSuccess(command ? booking.toCommand) {
              case Good(bookingId) =>
                complete(StatusCodes.OK, NewEnquiryResponse(bookingId match {
                  case x: BookingId => x.id
                  case _            => 0l
                }))
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
    } ~
    path("depositPaid") {
      post {
        decodeRequest {
          entity(as[DepositPaidRequest]) { depositPaid =>
            onSuccess(command ? depositPaid.toCommand) {
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
