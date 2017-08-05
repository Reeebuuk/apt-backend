//package hr.com.blanka.apartments
//
//import akka.actor.{ ActorRef, ActorSystem }
//import akka.event.{ LoggingAdapter, NoLogging }
//import akka.http.scaladsl.model.StatusCodes._
//import akka.http.scaladsl.model._
//import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
//import com.typesafe.config.Config
//import hr.com.blanka.apartments.Main._
//import hr.com.blanka.apartments.command.CommandActor
//import hr.com.blanka.apartments.http.model.{ LookupPriceForRangeRequest, PriceForRangeResponse, SavePriceRangeRequest }
//import hr.com.blanka.apartments.query.QueryActor
//import java.time.LocalDate
//import org.scalatest.{ FlatSpec, Matchers }
//import org.scalatest.concurrent.Eventually
//import org.scalatest.time.{ Second, Seconds, Span }
//import spray.json._
//
//import scala.concurrent.ExecutionContextExecutor
//import scala.concurrent.duration._
//import scala.language.implicitConversions
//
//class PriceTest
//  extends FlatSpec
//  with Matchers
//  with ScalatestRouteTest
//  with Eventually {
//
//  protected val log: LoggingAdapter = NoLogging
//
//  override def testConfig: Config = IntegrationConf.config(IntegrationConf.freePort, classOf[PriceTest].getSimpleName)
//
//  implicit val ec: ExecutionContextExecutor = system.dispatcher
//
//  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(new DurationInt(10).second)
//
//  val command: ActorRef = system.actorOf(CommandActor(), "commandActor")
//  val query: ActorRef = system.actorOf(QueryActor(materializer), "queryActor")
//
//  val midYearDate: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(5)
//
//  implicit val config = PatienceConfig(Span(5, Seconds), Span(1, Second))
//
//  "Price service" should "save multiple prices and fetch results" in {
//    val userId = "user"
//    val unitId = 1
//
//    val firstFrom = midYearDate
//    val firstTo = firstFrom.plusDays(5)
//    val firstPrice = SavePriceRangeRequest(userId, unitId, firstFrom, firstTo, 35)
//    val firstRequestEntity = HttpEntity(MediaTypes.`application/json`, firstPrice.toJson.toString())
//
//    Post("/price", firstRequestEntity) ~> commandPriceRoute(command) ~> check {
//      status should be(OK)
//    }
//
//    val secondFrom = firstTo
//    val secondTo = secondFrom.plusDays(5)
//    val secondPrice = SavePriceRangeRequest(userId, unitId, secondFrom, secondTo, 40)
//    val secondRequestEntity = HttpEntity(MediaTypes.`application/json`, secondPrice.toJson.toString())
//
//    Post("/price", secondRequestEntity) ~> commandPriceRoute(command) ~> check {
//      status should be(OK)
//    }
//
//    val from = midYearDate.plusDays(3)
//    val to = from.plusDays(4)
//    val lookupRequest =
//      HttpEntity(MediaTypes.`application/json`, LookupPriceForRangeRequest(userId, unitId, from, to).toJson.toString())
//
//    eventually {
//      Post("/price/calculate", lookupRequest) ~> queryPriceRoute(query) ~> check {
//        responseAs[PriceForRangeResponse] should be(PriceForRangeResponse(150))
//        status should be(OK)
//      }
//    }
//  }
//}
