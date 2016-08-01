package hr.com.blanka.apartments.command.price

import akka.actor.{Actor, ActorRefFactory, ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit, TestProbe}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime
import org.scalactic.Good
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration._
import scala.language.postfixOps

class CommandPriceActorTest extends TestKit(ActorSystem("test-benefits",
  ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"] """))) with FlatSpecLike with Matchers
  with BeforeAndAfterAll with Eventually{

  class MockedChild extends Actor {
    def receive = {
      case SavePriceForSingleDay(userId, unitId, day, price) =>
         sender() ! DailyPriceSaved(userId, unitId, day, price, new DateTime())
    }
  }

  val probe = TestProbe()
  val mockProps = Props(classOf[MockedChild], this)
  val commandPriceActor = TestActorRef(new CommandPriceActor(mockProps))

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

    probe.send(commandPriceActor, firstPrice)
    eventually {
      probe.expectMsg(Good)
    }
  }
}
