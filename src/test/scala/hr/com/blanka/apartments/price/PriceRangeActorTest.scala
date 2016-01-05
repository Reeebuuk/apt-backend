package hr.com.blanka.apartments.price

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import hr.com.blanka.apartments.price.PriceQueryProtocol.{CalculatePriceForRange, PriceForRangeCalculated, PriceQueryResponse}
import hr.com.blanka.apartments.utils.{PricingConfig, AppConfig, DateUtils}
import com.typesafe.config.ConfigFactory
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class PriceRangeActorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with DateUtils with AppConfig with Eventually {

  implicit override val patienceConfig =
    PatienceConfig(timeout = scaled(1 second), interval = scaled(100 milliseconds))

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

  "A PriceRangeActor" should {

    "return value for single day" in {
      val today = midYearDate.getMillis
      val tomorrow = afterDay(today)
      val pricePromise = Promise[PriceQueryResponse]()
      val calculatePriceForRangeForSingleDay = CalculatePriceForRange(1, today, tomorrow, pricePromise)

      val concertActor = _system.actorOf(PriceRangeActor(PricingConfig(pricingConfig)))
      concertActor ! calculatePriceForRangeForSingleDay

      eventually {
        pricePromise.isCompleted shouldBe true
        pricePromise.future.value.get shouldBe Success(PriceForRangeCalculated(BigDecimal(35)))
      }
    }

    "return value for multiple days" in {
      val today = midYearDate.getMillis
      val tomorrow = midYearDate.plusDays(7).getMillis
      val pricePromise = Promise[PriceQueryResponse]()
      val calculatePriceForRangeForMultipleDays = CalculatePriceForRange(1, today, tomorrow, pricePromise)

      val concertActor = _system.actorOf(PriceRangeActor(PricingConfig(pricingConfig)))
      concertActor ! calculatePriceForRangeForMultipleDays

      eventually {
        pricePromise.isCompleted shouldBe true
        pricePromise.future.value.get shouldBe Success(PriceForRangeCalculated(BigDecimal(245)))
      }
    }

//TODO filed promise? invalid stuff?

  }
}
