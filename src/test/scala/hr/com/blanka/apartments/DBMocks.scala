package hr.com.blanka.apartments

import hr.com.blanka.apartments.model.PriceForRange
import org.joda.time.DateTime
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.Macros

import scala.concurrent.ExecutionContext.Implicits.global

trait DBMocks extends Configured {

  implicit val priceForRangeFormat = Macros.handler[PriceForRange]

  def insertPrices() = {

    val dataSource = configured[DefaultDB]
    val collection = dataSource(priceForRange).asInstanceOf[BSONCollection]

    collection.insert(PriceForRange(1, new DateTime().withDayOfMonth(1).withMonthOfYear(1).getMillis,
      new DateTime().withDayOfMonth(31).withMonthOfYear(12).getMillis, 50, new DateTime().getMillis))
  }
}
