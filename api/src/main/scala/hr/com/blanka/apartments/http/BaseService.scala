package hr.com.blanka.apartments.http

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import hr.com.blanka.apartments.http.routes.command.{ CommandBookingServiceRoute, CommandPriceServiceRoute }
import hr.com.blanka.apartments.http.routes.query.{ QueryBookingServiceRoute, QueryPriceServiceRoute }

trait BaseService
    extends QueryPriceServiceRoute
    with QueryBookingServiceRoute
    with CommandBookingServiceRoute
    with CommandPriceServiceRoute {

  def routes(command: ActorRef, query: ActorRef): Route = {
    pathPrefix("v1") {
      queryPriceRoute(query) ~ queryBookingRoute(query) ~
        commandPriceRoute(command) ~ commandBookingRoute(command)
    }
  }
}
