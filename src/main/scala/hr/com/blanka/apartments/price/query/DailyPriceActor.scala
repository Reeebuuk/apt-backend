package hr.com.blanka.apartments.price.query

import akka.actor.{Actor, ActorLogging, Props}
import hr.com.blanka.apartments.Configured
import hr.com.blanka.apartments.model.PriceForRange
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.api.{DefaultDB, ReadPreference}
import reactivemongo.bson.{BSONDocument, Macros}

import scala.concurrent.ExecutionContext.Implicits.global

object DailyPriceActor {

  sealed trait DailyPriceMsg

  sealed trait Query extends DailyPriceMsg

  case class CalculatePriceForDay(id: Long, unitId: Int, day: Long) extends Query

  sealed trait QueryResponse extends DailyPriceMsg {
    def requestId: Long
  }

  case class DailyPriceCalculated(requestId: Long, day: Long, price: Int) extends QueryResponse

  case class DailyPriceCannotBeCalculated(requestId: Long) extends QueryResponse

  def apply() = Props(classOf[DailyPriceActor])

}

class DailyPriceActor extends Actor with ActorLogging with Configured {

  import DailyPriceActor._

  implicit val priceForRangeFormat = Macros.handler[PriceForRange]

  override def receive: Receive = {
    case CalculatePriceForDay(requestId, unitId, day) => {
      val dataSource = configured[DefaultDB]
      val collection = dataSource(priceForRange).asInstanceOf[BSONCollection]
      val sendTo = sender()

      val priceForRangeForUnit = collection.find(BSONDocument("unitId" -> unitId)).
        cursor[PriceForRange](ReadPreference.primaryPreferred).
        collect[List]()

      val price = priceForRangeForUnit.map {
        _.filter(x => x.from <= day && x.to >= day)
         .maxBy(_.created).price
      }

      price.map(sendTo ! DailyPriceCalculated(requestId, day, _))
    }
  }

}