package hr.com.blanka.apartments.http.routes

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import hr.com.blanka.apartments.command.booking.{DepositPaid, EnquiryReceived}
import hr.com.blanka.apartments.query.booking.{AvailableApartments, BookedDays, GetAvailableApartments, GetBookedDates}
import hr.com.blanka.apartments.utils.MarshallingSupport
import org.scalactic._

final case class ErrorResponse(msg: String)

trait BookingServiceRoute extends BaseServiceRoute with MarshallingSupport {

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def bookingRoute(command: ActorRef, query: ActorRef) = pathPrefix("booking") {
    pathEndOrSingleSlash {
      post {
        decodeRequest {
          entity(as[EnquiryReceived]) { booking =>
            onSuccess(command ? booking) {
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
      path("depositPaid") {
        post {
          decodeRequest {
            entity(as[DepositPaid]) { depositPaid =>
              onSuccess(command ? depositPaid) {
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
      path("bookedDates") {
        parameter("unitId".as[Int]) { unitId =>
          onSuccess(query ? GetBookedDates("user", unitId)) {
            case Good(result: BookedDays) => complete(StatusCodes.OK, result)
            case Bad(response) => response match {
              case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
              case Many(first, second) => complete(StatusCodes.BadRequest, ErrorResponse(Seq(first, second).mkString(", ")))
              case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
            }
          }
        }
      } ~
      path("available") {
        parameters('from.as[Long], 'to.as[Long]) { (from, to) =>
          onSuccess(query ? GetAvailableApartments("user", from, to)) {
            case Good(result: AvailableApartments) => complete(StatusCodes.OK, result)
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