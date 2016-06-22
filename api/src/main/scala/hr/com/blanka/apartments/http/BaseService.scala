package hr.com.blanka.apartments.http

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import hr.com.blanka.apartments.http.routes._

trait BaseService extends PriceServiceRoute with BookingServiceRoute {

  def routes(implicit system: ActorSystem) =
    pathPrefix("v1") {
      priceRoute ~ bookingRoute
    }

}
