package hr.com.blanka.apartments.http.routes

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.price.{CommandPriceRangeActor, DailyPriceAggregateActor, QueryPriceRangeActor}
import hr.com.blanka.apartments.price.protocol.{InvalidRange, LookupPriceForRange, PriceForRangeCalculated, SavePriceRange}
import hr.com.blanka.apartments.utils.MarshallingSupport
import org.scalactic._

import scala.concurrent.duration._
import scala.language.postfixOps

final case class PriceForRangeResponse(price: Int)

final case class ErrorResponse(msg: String)

trait PriceServiceRoute extends BaseServiceRoute with MarshallingSupport {

  implicit val timeout = Timeout(9 seconds)

  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

  def priceRoute(implicit system: ActorSystem) = pathPrefix("price") {

    val aggregate = system.actorOf(Props(classOf[DailyPriceAggregateActor]), "dailyPriceActor")

    val command = system.actorOf(Props(classOf[CommandPriceRangeActor], aggregate), "commandActor")
    val query = system.actorOf(Props(classOf[QueryPriceRangeActor], aggregate), "queryActor")

    pathEndOrSingleSlash {
      post {
        decodeRequest {
          entity(as[SavePriceRange]) { savePriceRange =>
            onSuccess(command ? savePriceRange) {
              case Good => complete(StatusCodes.OK)
              case Bad(response) => response match {
                case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                case Many(first, second) => complete(StatusCodes.BadRequest, ErrorResponse(Seq(first, second).mkString(", ")))
                case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
              }
            }
          }
        }
      }
    } ~
      path("calculate") {
        post {
          decodeRequest {
            entity(as[LookupPriceForRange]) { lookupPriceForRange =>
              onSuccess(query ? lookupPriceForRange) {
                case Good(result) => complete(StatusCodes.OK, PriceForRangeResponse(result.asInstanceOf[Int]))
                case Bad(response) => response match {
                  case One(error) => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                  case Many(first, second) => complete(StatusCodes.BadRequest, ErrorResponse(Seq(first, second).mkString(", ")))
                  case error => complete(StatusCodes.BadRequest, ErrorResponse(error.toString))
                }
              }
            }
          }
        }
      }
  }
}