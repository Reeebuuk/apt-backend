package hr.com.blanka.apartments

import java.io.File

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.testkit.{ RouteTestTimeout, ScalatestRouteTest }
import akka.persistence.cassandra.testkit.CassandraLauncher
import hr.com.blanka.apartments.command.CommandActor
import hr.com.blanka.apartments.query.QueryActor
import org.scalatest.concurrent.{ Eventually, ScalaFutures }
import org.scalatest.time.{ Seconds, Span }
import org.scalatest.{ AsyncWordSpec, Matchers }

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

trait BaseIntegrationTest
    extends AsyncWordSpec
    with Matchers
    with ScalatestRouteTest
    with Eventually
    with TestMarshallingSupport
    with ScalaFutures {

  CassandraLauncher.start(
    new File("cassandra"),
    CassandraLauncher.DefaultTestConfigResource,
    clean = true,
    port = 9043
  )

  implicit def default(implicit system: ActorSystem): RouteTestTimeout =
    RouteTestTimeout(new DurationInt(10).second)

  val command: ActorRef = system.actorOf(CommandActor(), "commandActor")
  val query: ActorRef   = system.actorOf(QueryActor(materializer), "queryActor")

  implicit val config: PatienceConfig = PatienceConfig(Span(10, Seconds), Span(2, Seconds))

  // Would like to find a cleaner way
  implicit class extractor[T](x: Future[T]) {
    def eagerExtract: T =
      x.value.get.get
  }

}
