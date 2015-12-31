package com.example.crudapi.price

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.example.crudapi.price.PriceCommandQueryProtocol.{CalculatePriceForRange, PriceForRangeCalculated, PriceQueryResponse}
import com.example.crudapi.utils.{DateUtils, PricingConfig}
import com.typesafe.config.ConfigFactory
import org.joda.time.{DateTime, DateTimeZone}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.Promise

class PriceRangeActorTest(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
with WordSpecLike with Matchers with BeforeAndAfterAll with DateUtils {
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

  val priceConfig: PricingConfig = PricingConfig(ConfigFactory.load("pricing"))

  "A PriceRangeActor" when {
    val today = new DateTime().toDateTime(DateTimeZone.UTC).withTime(12, 0, 0, 0).getMillis
    val tomorrow = afterDay(today)
    val pricePromise = Promise[PriceQueryResponse]()
    val calculatePriceForRangeForSingleDay = CalculatePriceForRange(1, today, tomorrow, pricePromise)

    s"receiving '$calculatePriceForRangeForSingleDay'" should {
      s"return value for single day" in {
        val concertActor = _system.actorOf(PriceRangeActor(priceConfig))
        concertActor ! calculatePriceForRangeForSingleDay

        expectMsg(PriceForRangeCalculated(BigDecimal(35)))
      }
    }

    val weekAfter = new DateTime(today).plusDays(7).getMillis
    val pricePromise1 = Promise[PriceQueryResponse]()

    val calculatePriceForRangeForWeek = CalculatePriceForRange(1, today, weekAfter, pricePromise1)

    s"receiving '$calculatePriceForRangeForWeek'" should {
      s"return value for week" in {
        val concertActor = _system.actorOf(PriceRangeActor(priceConfig))
        concertActor ! calculatePriceForRangeForWeek

        expectMsg(PriceForRangeCalculated(BigDecimal(245)))
      }
    }

    /*    val fromDifferentZones = new DateTime().toDateTime(DateTimeZone.UTC).withDate(2015, 7, 19).withTime(12, 0, 0, 0).getMillis
        val toDifferentZones = new DateTime(fromDifferentZones).plusDays(7).getMillis
        val calculatePriceForRangeForWeekDifferentZones = CalculatePriceForRange(1, fromDifferentZones, toDifferentZones)

        s"receiving '$calculatePriceForRangeForWeekDifferentZones'" should {
          s"return value for week in different zones" in {
            val concertActor = _system.actorOf(PriceRangeActor.props(priceConfig))
            concertActor ! calculatePriceForRangeForWeekDifferentZones

            expectMsg(PriceForRangeCalculated(1, BigDecimal(340)))
          }
        }

        val fromDifferentYear = new DateTime().toDateTime(DateTimeZone.UTC).withDate(2015, 12, 30).withTime(12, 0, 0, 0).getMillis
        val toDifferentYear = new DateTime(fromDifferentYear).plusDays(7).getMillis
        val calculatePriceForRangeForWeekDifferentYear = CalculatePriceForRange(1, fromDifferentYear, toDifferentYear)

        s"receiving '$calculatePriceForRangeForWeekDifferentYear'" should {
          s"return value for week in different years" in {
            val concertActor = _system.actorOf(PriceRangeActor.props(priceConfig))
            concertActor ! calculatePriceForRangeForWeekDifferentYear

            expectMsg(PriceForRangeCalculated(1, BigDecimal(245)))
          }
        }*/

  }
}
