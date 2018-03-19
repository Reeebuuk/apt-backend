package hr.com.blanka.apartments.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, ExceptionHandler, RejectionHandler, Route}
import hr.com.blanka.apartments.http.routes.command.{CommandContactServiceRoute, CommandEnquiryServiceRoute, CommandPriceServiceRoute}
import hr.com.blanka.apartments.http.routes.query.{QueryBookingServiceRoute, QueryEnquiryServiceRoute, QueryPriceServiceRoute}

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

  val handleErrors: Directive[Unit] = handleRejections(RejectionHandler.default) & handleExceptions(
    exceptionHandler
  )

  def routes(command: ActorRef, query: ActorRef): Route =
        handleErrors {
          pathPrefix("v1") {
            queryPriceRoute(query) ~ queryBookingRoute(query) ~ queryEnquiryRoute(query) ~
            commandPriceRoute(command) ~ commandEnquiryRoute(command) ~ commandContactRoute(command)
          }
        }
}
