package hr.com.blanka.apartments.http.routes.query

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.common.ValueClasses._
import hr.com.blanka.apartments.http.model._
import hr.com.blanka.apartments.http.routes.BaseServiceRoute
import hr.com.blanka.apartments.query.booking._
import hr.com.blanka.apartments.utils.WriteMarshallingSupport
import org.scalactic._

trait QueryEnquiryServiceRoute extends BaseServiceRoute with WriteMarshallingSupport {

  import Directives._
  import PlayJsonSupport._

  def queryEnquiryRoute(query: ActorRef): Route = pathPrefix("enquiry") {
    path("approved") {
      parameter("year".as[Int]) { year =>
        onSuccess(query ? GetAllApprovedEnquiries(UserId("user"), year)) {
          case Good(x: AllApprovedEnquiries) =>
            complete(StatusCodes.OK, AllApprovedEnquiriesResponse.remap(x))
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
    path("unapproved") {
      parameter("year".as[Int]) { year =>
        onSuccess(query ? GetAllUnapprovedEnquiries(UserId("user"), year)) {
          case Good(x: AllUnapprovedEnquiries) =>
            complete(StatusCodes.OK, AllUnapprovedEnquiriesResponse.remap(x))
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
