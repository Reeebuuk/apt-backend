package hr.com.blanka.apartments.command.price

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.joda.time.DateTime
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import akka.pattern.ask
import akka.util.Timeout
import org.scalactic
import org.scalactic.Good
import org.scalatest.concurrent.{Eventually, IntegrationPatience}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class CommandPriceActorTest extends TestKit(ActorSystem("test-benefits")) with FlatSpecLike with Matchers
  with BeforeAndAfterAll with Eventually{

  val commandPriceActor = system.actorOf(CommandPriceActor())

  implicit val timeout = Timeout(3 seconds)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "SavePriceRange event" should "return Good result if save range is valid" in {
    val userId = "user"
    val unitId = 1

    val firstFrom = new DateTime()
    val firstTo = firstFrom.plusDays(5)
    val firstPrice = SavePriceRange(userId, unitId, firstFrom.getMillis, firstTo.getMillis, 35)

    eventually {
      val futureResult = commandPriceActor ? firstPrice
      val Success(result: Int) = futureResult.value.get
      result should be(Good)
    }
  }
}
