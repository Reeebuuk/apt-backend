package hr.com.blanka.apartments.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.HttpMethods.{ GET, POST, PUT }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.{ HttpOrigin, HttpOriginRange }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directive, ExceptionHandler, RejectionHandler, Route }
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.{ cors, corsRejectionHandler }
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import hr.com.blanka.apartments.http.routes.command.{
  CommandContactServiceRoute,
  CommandEnquiryServiceRoute,
  CommandPriceServiceRoute
}
import hr.com.blanka.apartments.http.routes.query.{
  QueryBookingServiceRoute,
  QueryEnquiryServiceRoute,
  QueryPriceServiceRoute
}

trait BaseService
    extends QueryPriceServiceRoute
    with QueryBookingServiceRoute
    with QueryEnquiryServiceRoute
    with CommandEnquiryServiceRoute
    with CommandPriceServiceRoute
    with CommandContactServiceRoute {

  val corsSettings: CorsSettings.Default = CorsSettings.defaultSettings.copy(
    allowedOrigins = HttpOriginRange(HttpOrigin("http://www.apartments-blanka.com.hr")),
    allowedMethods = List(GET, POST, PUT)
  )

  val rejectionHandler
    : RejectionHandler = corsRejectionHandler withFallback RejectionHandler.default

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException => complete(StatusCodes.NotFound -> e.getMessage)
  }

  val handleErrors: Directive[Unit] = handleRejections(rejectionHandler) & handleExceptions(
    exceptionHandler
  )

  def routes(command: ActorRef, query: ActorRef): Route =
    handleErrors {
//      cors(corsSettings) {
        handleErrors {
          pathPrefix("v1") {
            queryPriceRoute(query) ~ queryBookingRoute(query) ~ queryEnquiryRoute(query) ~
            commandPriceRoute(command) ~ commandEnquiryRoute(command) ~ commandContactRoute(command)
          }
        }
//      }
    }
}
