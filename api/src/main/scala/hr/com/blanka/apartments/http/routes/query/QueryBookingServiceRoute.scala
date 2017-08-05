package hr.com.blanka.apartments.http.routes.query

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import hr.com.blanka.apartments.http.model.{
  AvailableApartmentsResponse,
  BookedDaysResponse,
  ErrorResponse
}
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.query.booking.{
  AvailableApartments,
  BookedDays,
  GetAvailableApartments,
  GetBookedDates
}
import hr.com.blanka.apartments.utils.WriteMarshallingSupport
import java.time.LocalDate

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import org.scalactic._

trait QueryBookingServiceRoute extends BaseServiceRoute with WriteMarshallingSupport {

  import Directives._
  import PlayJsonSupport._

  def queryBookingRoute(query: ActorRef): Route = pathPrefix("booking") {
    path("bookedDates") {
      parameter("unitId".as[Int]) { unitId =>
        onSuccess(query ? GetBookedDates("user", unitId)) {
          case Good(bd: BookedDays) => complete(StatusCodes.OK, BookedDaysResponse.remap(bd))
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
    path("available") {
      parameters('from.as[Long], 'to.as[Long]) { (from, to) =>
        onSuccess(
          query ? GetAvailableApartments("user",
                                         LocalDate.ofEpochDay(from),
                                         LocalDate.ofEpochDay(to))
        ) {
          case Good(aa: AvailableApartments) =>
            complete(StatusCodes.OK, AvailableApartmentsResponse.remap(aa))
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
