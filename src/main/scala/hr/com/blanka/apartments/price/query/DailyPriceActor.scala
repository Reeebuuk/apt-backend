package hr.com.blanka.apartments.price.query

import akka.actor.{Actor, ActorLogging, Props}
import hr.com.blanka.apartments.Configured
import hr.com.blanka.apartments.utils.PricingConfig
import org.joda.time.DateTime
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.ExecutionContext.Implicits.global


object DailyPriceActor {

  sealed trait DailyPriceMsg

  sealed trait Query extends DailyPriceMsg

  case class CalculatePriceForDay(id: Long, unitId: Int, day: Long) extends Query

  sealed trait QueryResponse extends DailyPriceMsg {
    def requestId: Long
  }

  case class DailyPriceCalculated(requestId: Long, day: Long, price: BigDecimal) extends QueryResponse
  case class DailyPriceCannotBeCalculated(requestId: Long) extends QueryResponse

  def apply(pricingConfig: PricingConfig) = Props(classOf[DailyPriceActor], pricingConfig)

}

class DailyPriceActor(pricingConfig: PricingConfig) extends Actor with ActorLogging with Configured {

  import DailyPriceActor._

  override def receive: Receive = {
    case CalculatePriceForDay(requestId, unitId, day) => {
      val dataSource = configured[DefaultDB]
      val collection = dataSource("test.collections").asInstanceOf[BSONCollection]

      val lala = collection.count()

      pricingConfig.pricings
        .filter(x => x.from <= day && x.to >= day)
        .map(x => x.appPrice(unitId)) match {
        case price :: Nil => sender() ! DailyPriceCalculated(requestId, day, price)
        case price => {
          log.error(s"Cannot find price for unitId: $unitId, day: ${new DateTime(day)} ($day). Found $price")
          sender() ! DailyPriceCannotBeCalculated(requestId)
        }
      }
    }
  }

}