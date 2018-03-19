package hr.com.blanka.apartments.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Directive, ExceptionHandler, RejectionHandler, Route }
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
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

trait BaseService
    extends QueryPriceServiceRoute
    with QueryBookingServiceRoute
    with QueryEnquiryServiceRoute
    with CommandEnquiryServiceRoute
    with CommandPriceServiceRoute
    with CommandContactServiceRoute {

  val exceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: NoSuchElementException => complete(StatusCodes.NotFound -> e.getMessage)
  }

  val settings: CorsSettings.Default =
    CorsSettings.defaultSettings.copy(allowGenericHttpRequests = true)

  val handleErrors: Directive[Unit] = handleRejections(RejectionHandler.default) & handleExceptions(
    exceptionHandler
  )

  def routes(command: ActorRef, query: ActorRef): Route =
    cors(settings) {
      handleErrors {
        pathPrefix("v1") {
          queryPriceRoute(query) ~ queryBookingRoute(query) ~ queryEnquiryRoute(query) ~
          commandPriceRoute(command) ~ commandEnquiryRoute(command) ~ commandContactRoute(command)
        }
      }
    }
}
