package hr.com.blanka.apartments

import hr.com.blanka.apartments.model.PriceForRange
import org.joda.time.DateTime
import reactivemongo.api.{ReadPreference, DefaultDB}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, Macros}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

trait DBMocks extends Configured {

  implicit val priceForRangeFormat = Macros.handler[PriceForRange]

  def insertPrices() = {

    val dataSource = configured[DefaultDB]
    val collection = dataSource(priceForRange).asInstanceOf[BSONCollection]

    collection.insert(PriceForRange(1, new DateTime().withDayOfMonth(1).withMonthOfYear(1).getMillis,
      new DateTime().withDayOfMonth(31).withMonthOfYear(12).getMillis, 50, new DateTime().getMillis))

    val priceForRangeForUnit = collection.find(BSONDocument("unitId" -> 1)).
      cursor[PriceForRange](ReadPreference.primaryPreferred).
      collect[List]()

    val lala = Await.ready(priceForRangeForUnit, Duration.Inf).value.get
    lala
  }
}
