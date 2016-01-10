package hr.com.blanka.apartments.price.command

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import hr.com.blanka.apartments.{Configured, TestMongoDbConfiguration}
import hr.com.blanka.apartments.http.routes.SavePriceForRangeDto
import hr.com.blanka.apartments.price.PriceCommandProtocol.{PriceForRangeSaved, SavePriceCommandResponse}
import hr.com.blanka.apartments.utils.{AppConfig, DateUtils}
import org.joda.time.{DateTime, DateTimeZone}
import org.scalactic.Good
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

import scala.concurrent.ExecutionContext.Implicits.global

class CommandPriceRangeActorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with DateUtils with AppConfig with Eventually
with TestMongoDbConfiguration with Configured{

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

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

  val midYearDate = new DateTime().toDateTime(DateTimeZone.UTC).withMonthOfYear(6).withDayOfMonth(5).withTime(12, 0, 0, 0)

  "CommandPriceRangeActorTest" should {

    "save a price for a single day" in {
      val today = midYearDate.getMillis
      val tomorrow = afterDay(today)
      val saveMessage = SavePriceForRangeDto(1, today, tomorrow, 50)

      val actor = _system.actorOf(CommandPriceRangeActor())
      val future = actor ? saveMessage

      val dataSource = configured[DefaultDB]
      val collection = dataSource("priceForRange").asInstanceOf[BSONCollection]

      eventually {
        future.isCompleted shouldBe true
        future.value.get shouldBe Success(SavePriceCommandResponse(Good(PriceForRangeSaved)))

        collection.count().value.get shouldBe Success(1)
      }
    }
  }
}
