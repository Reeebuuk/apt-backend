package hr.com.blanka.apartments.price.query

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import hr.com.blanka.apartments.price.PriceQueryProtocol.{LookupPriceForRange, PriceForRangeCalculated, PriceQueryResponse}
import hr.com.blanka.apartments.{DBMocks, IntegrationTestMongoDbSupport}
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.concurrent.Eventually
import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Success

class QueryPriceRangeActorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with Eventually with IntegrationTestMongoDbSupport with DBMocks {

  implicit override val patienceConfig =
    PatienceConfig(timeout = scaled(2 second), interval = scaled(100 milliseconds))

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

  override def beforeAll {
    super.beforeAll
    insertPrices()
  }

  override def afterAll {
    TestKit.shutdownActorSystem(system)
    super.afterAll
  }

  val midYearDate = new DateTime().toDateTime(DateTimeZone.UTC).withMonthOfYear(6).withDayOfMonth(5).withTime(12, 0, 0, 0)

  "QueryPriceRangeActor" should {

    "return value for single day" in {
      val today = midYearDate.getMillis
      val tomorrow = midYearDate.plusDays(1).getMillis
      val pricePromise = Promise[PriceQueryResponse]()
      val calculatePriceForRangeForSingleDay = LookupPriceForRange(1, today, tomorrow, pricePromise)

      val actor = _system.actorOf(QueryPriceRangeActor())
      actor ! calculatePriceForRangeForSingleDay

      eventually {
        pricePromise.isCompleted shouldBe true
        pricePromise.future.value.get shouldBe Success(PriceForRangeCalculated(50))
      }
    }

/*    "return value for multiple days" in {
      val today = midYearDate.getMillis
      val tomorrow = midYearDate.plusDays(7).getMillis
      val pricePromise = Promise[PriceQueryResponse]()
      val calculatePriceForRangeForMultipleDays = LookupPriceForRange(1, today, tomorrow, pricePromise)

      val actor = _system.actorOf(QueryPriceRangeActor())
      actor ! calculatePriceForRangeForMultipleDays

      eventually {
        pricePromise.isCompleted shouldBe true
        pricePromise.future.value.get shouldBe Success(PriceForRangeCalculated(BigDecimal(245)))
      }
    }*/

//TODO filed promise? invalid stuff?

  }
}
