package hr.com.blanka.apartments.http.routes

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.booking.protocol.{GetAvailableApartments, GetBookedDates, SaveBooking}
import hr.com.blanka.apartments.price.protocol.{InvalidRange, LookupPriceForRange, PriceForRangeCalculated, SavePriceRange}
import hr.com.blanka.apartments.utils.MarshallingSupport
import org.scalactic._

import scala.concurrent.duration._
import scala.language.postfixOps

final case class ErrorResponse(msg: String)

trait BookingServiceRoute extends BaseServiceRoute with MarshallingSupport {

  implicit val timeout = Timeout(9 seconds)

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def bookingRoute(implicit system: ActorSystem) = pathPrefix("booking") {
    pathEndOrSingleSlash {
      post {
        decodeRequest {
          entity(as[SaveBooking]) { booking =>
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
    path("bookedDates") {
      parameter("apartmentId".as[Int]) { apartmentId =>
        onSuccess(query ? GetBookedDates("", apartmentId)) {
          case Good(result) => complete(StatusCodes.OK, PriceForRangeResponse(result.asInstanceOf[Int]))
          case Bad(response) => response match {
            case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
            case Many(first, second) => complete(StatusCodes.BadRequest, ErrorResponse(Seq(first, second).mkString(", ")))
            case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
          }
        }
      }
    } ~
    path("available") {
      parameters('from.as[Long], 'to.as[Long]){ (from, to) =>
        onSuccess(query ? GetAvailableApartments("", from, to)) {
          case Good(result) => complete(StatusCodes.OK, PriceForRangeResponse(result.asInstanceOf[Int]))
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