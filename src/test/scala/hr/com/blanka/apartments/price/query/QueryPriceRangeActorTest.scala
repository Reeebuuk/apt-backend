package hr.com.blanka.apartments.price.query

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.concurrent.Eventually
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._
import scala.language.postfixOps

class QueryPriceRangeActorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
 with Matchers with Eventually with WordSpecLike with BeforeAndAfterAll {

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


  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val midYearDate = new DateTime().toDateTime(DateTimeZone.UTC).withMonthOfYear(6).withDayOfMonth(5).withTime(12, 0, 0, 0)

  /*"QueryPriceRangeActor with single price" should {

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
  }

  "QueryPriceRangeActor with multiple price for same dates" should {

    "return value for single day with latest price" in {
      val newPrice = defaultPrice.copy(price = 52)
      insertPrices(Set(newPrice))

      val today = midYearDate.getMillis
      val tomorrow = midYearDate.plusDays(1).getMillis
      val pricePromise = Promise[PriceQueryResponse]()
      val calculatePriceForRangeForSingleDay = LookupPriceForRange(1, today, tomorrow, pricePromise)

      val actor = _system.actorOf(QueryPriceRangeActor())
      actor ! calculatePriceForRangeForSingleDay

      eventually {
        pricePromise.isCompleted shouldBe true
        pricePromise.future.value.get shouldBe Success(PriceForRangeCalculated(52))
      }
    }
  }
*/
}
