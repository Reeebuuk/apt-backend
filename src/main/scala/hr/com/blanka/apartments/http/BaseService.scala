package hr.com.blanka.apartments.http

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import hr.com.blanka.apartments.http.routes._

trait BaseService extends PriceServiceRoute {

  def routes(command: ActorRef, query: ActorRef) =
    pathPrefix("v1") {
      customersRoute(command, query)
    }

}
