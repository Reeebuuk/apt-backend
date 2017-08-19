package hr.com.blanka.apartments.http.routes.query

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import hr.com.blanka.apartments.http.model.{
  AllBookingsResponse,
  AvailableUnitsResponse,
  BookedDaysResponse,
  ErrorResponse
}
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.query.booking._
import hr.com.blanka.apartments.utils.WriteMarshallingSupport
import java.time.{ Instant, ZoneId }

import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.ValueClasses._
import org.scalactic._
import play.api.libs.json.Json

trait QueryBookingServiceRoute extends BaseServiceRoute with WriteMarshallingSupport {

  import Directives._
  import PlayJsonSupport._

  def queryBookingRoute(query: ActorRef): Route = pathPrefix("booking") {
    pathEndOrSingleSlash {
      get {
        onSuccess(query ? GetAllBookings(UserId("user"))) {
          case Good(bd: AllBookings) =>
            val lala = AllBookingsResponse.remap(bd)

            val hoho = Json.toJson[AllBookingsResponse](lala)
            complete(StatusCodes.OK, AllBookingsResponse.remap(bd))
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
    path("bookedDates") {
      parameter("unitId".as[Int]) { unitId =>
        onSuccess(query ? GetBookedDates(UserId("user"), UnitId(unitId))) {
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
          query ? GetAvailableUnits(
            userId = UserId("user"),
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
