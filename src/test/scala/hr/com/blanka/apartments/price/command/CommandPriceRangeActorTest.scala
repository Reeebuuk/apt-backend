package hr.com.blanka.apartments.price.command

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import hr.com.blanka.apartments.http.routes.SavePriceForRangeDto
import hr.com.blanka.apartments.model.PriceForRange
import hr.com.blanka.apartments.price.PriceCommandProtocol.{PriceForRangeSaved, SavePriceCommandResponse}
import hr.com.blanka.apartments.utils.AppConfig
import hr.com.blanka.apartments.{Configured, IntegrationTestMongoDbSupport}
import org.joda.time.LocalDate
import org.scalactic.Good
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.Macros

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class CommandPriceRangeActorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with Matchers with DateUtils with AppConfig with Eventually with IntegrationTestMongoDbSupport with Configured {

  implicit val timeout = Timeout(2 seconds)
  implicit override val patienceConfig =
    PatienceConfig(timeout = scaled(3 second), interval = scaled(100 milliseconds))

  def this() = this(
    ActorSystem("TestActorSystem", ConfigFactory.parseString(
      """
        |akka.loglevel = "DEBUG"
        |akka.persistence.journal.plugin = "in-memory-journal"
        |akka.actor.debug {
        |   receive = on
        |   autoreceive = on
        |   lifecycle = on
        |}
        |akka.actor.deployment {
        |  /parent/daily-price-calculators {
        |    router = round-robin-pool
        |    nr-of-instances = 5
        |  }
        |}
      """.stripMargin)))

  val dataSource = configured[DefaultDB]
  val collection = dataSource(priceForRange).asInstanceOf[BSONCollection]

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  val midYearDate = new LocalDate().withMonthOfYear(6).withDayOfMonth(5)

  "CommandPriceRangeActorTest" should {

    "save a price for a single day" in {
      val today = midYearDate.toDateTimeAtStartOfDay.getMillis
      val tomorrow = midYearDate.plusDays(1).toDateTimeAtStartOfDay.getMillis
      val saveMessage = SavePriceForRangeDto(1, today, tomorrow, 50)

      val actor = _system.actorOf(CommandPriceRangeActor())
      val future = actor ? saveMessage

      val count = collection.count()

      eventually {
        future.isCompleted shouldBe true
        future.value.get shouldBe Success(SavePriceCommandResponse(Good(PriceForRangeSaved)))

        count.isCompleted shouldBe true
        count.value.get shouldBe Success(1)
      }
    }

    "save a price for a multiple days" in {
      val today = midYearDate.toDateTimeAtStartOfDay.getMillis
      val tomorrow = midYearDate.plusDays(5).toDateTimeAtStartOfDay.getMillis
      val saveMessage = SavePriceForRangeDto(1, today, tomorrow, 50)

      val actor = _system.actorOf(CommandPriceRangeActor())
      val future = actor ? saveMessage

      val count = collection.count()

      eventually {
        future.isCompleted shouldBe true
        future.value.get shouldBe Success(SavePriceCommandResponse(Good(PriceForRangeSaved)))

        count.isCompleted shouldBe true
        count.value.get shouldBe Success(1)
      }
    }
  }
}
