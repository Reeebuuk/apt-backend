package hr.com.blanka.apartments.command.price

import java.time.{ LocalDate, LocalDateTime }

import akka.actor.{ Actor, ActorSystem, Props }
import akka.testkit.{ TestActorRef, TestKit, TestProbe }
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import hr.com.blanka.apartments.validation.ErrorMessages._
import org.scalactic.{ Bad, Good }
import org.scalatest.{ BeforeAndAfterAll, FreeSpecLike, Matchers }

import scala.concurrent.duration._
import scala.language.postfixOps

class CommandPriceActorTest
    extends TestKit(
      ActorSystem("test-benefits",
                  ConfigFactory
                    .parseString("""akka.loggers = ["akka.testkit.TestEventListener"] """))
    )
    with FreeSpecLike
    with Matchers
    with BeforeAndAfterAll {

  val probe = TestProbe()

  implicit val timeout = Timeout(1 seconds)

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  class MockPricePersistActor extends Actor {
    def receive = {
      case SavePriceForSingleDay("notSaving", unitId, day, price) =>
      case SavePriceForSingleDay(userId, unitId, day, price) =>
        sender() ! DailyPriceSaved(userId, unitId, day, price, LocalDateTime.now())
    }
  }

  val mockProps = Props(classOf[MockPricePersistActor], this)
  val commandPriceActor = TestActorRef(new CommandPriceActor(mockProps) {
    override val timeout = Timeout(100 milliseconds)
  })

  "SavePriceRange" - {
    val userId = "user"
    val unitId = 1
    "return Good result if save range is valid" in {
      val from           = LocalDate.now()
      val to             = from.plusDays(5)
      val savePriceRange = SavePriceRange(userId, unitId, from, to, 35)

      probe.send(commandPriceActor, savePriceRange)
      probe.expectMsg(Good)
    }

    "return Bad result if range is not valid" in {
      val from           = LocalDate.now()
      val to             = from.plusDays(5)
      val savePriceRange = SavePriceRange(userId, unitId, to, from, 35)

      probe.send(commandPriceActor, savePriceRange)
      probe.expectMsg(Bad(toDateBeforeFromDateErrorMessage(to, from)))
    }

    "return Bad result if range dates are in the past" in {
      val from           = LocalDate.now().minusDays(10)
      val to             = from.plusDays(5)
      val savePriceRange = SavePriceRange(userId, unitId, from, to, 35)

      probe.send(commandPriceActor, savePriceRange)
      probe.expectMsg(
        Bad(
          List(dateIsInPastErrorMessage("From", from), dateIsInPastErrorMessage("To", to))
            .mkString(", ")
        )
      )
    }

    "return Bad result if whole range is not successfully saved" in {
      val userId         = "notSaving"
      val from           = LocalDate.now()
      val to             = from.plusDays(5)
      val savePriceRange = SavePriceRange(userId, unitId, from, to, 35)

      probe.send(commandPriceActor, savePriceRange)
      probe.expectMsg(Bad(persistingDailyPricesErrorMessage))
    }
  }
}
