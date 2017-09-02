package hr.com.blanka.apartments.http.routes.query

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import hr.com.blanka.apartments.http.model.{
  AvailableUnitsResponse,
  BookedDatesResponse,
  ErrorResponse
}
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.query.booking._
import hr.com.blanka.apartments.utils.WriteMarshallingSupport
import java.time.{ Instant, ZoneId }

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.common.ValueClasses._
import org.scalactic._

trait QueryBookingServiceRoute extends BaseServiceRoute with WriteMarshallingSupport {

  import Directives._
  import PlayJsonSupport._

  def queryBookingRoute(query: ActorRef): Route = pathPrefix("booking") {
    path("bookedDates") {
      extractUser { userId =>
        parameter("unitId".as[Int]) { unitId =>
          onSuccess(query ? GetBookedDates(userId, UnitId(unitId))) {
            case Good(bd: BookedDays) => complete(StatusCodes.OK, BookedDatesResponse.remap(bd))
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
    } ~
    path("available") {
      extractUser { userId =>
        parameters('from.as[Long], 'to.as[Long]) { (from, to) =>
          onSuccess(
            query ? GetAvailableUnits(
              userId = userId,
              from = Instant.ofEpochMilli(from).atZone(ZoneId.of("UTC")).toLocalDate,
              to = Instant.ofEpochMilli(to).atZone(ZoneId.of("UTC")).toLocalDate
            )
          ) {
            case Good(aa: AvailableUnits) =>
              complete(StatusCodes.OK, AvailableUnitsResponse.remap(aa))
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
