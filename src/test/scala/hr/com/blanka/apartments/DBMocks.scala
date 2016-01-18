package hr.com.blanka.apartments

import hr.com.blanka.apartments.model.PriceForRange
import org.joda.time.DateTime
import reactivemongo.api.{ReadPreference, DefaultDB}
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, Macros}

import scala.concurrent.ExecutionContext.Implicits.global

trait DBMocks extends Configured {

  implicit val priceForRangeFormat = Macros.handler[PriceForRange]

  val defaultPrice = PriceForRange(1, new DateTime().withDayOfMonth(1).withMonthOfYear(1).getMillis,
    new DateTime().withDayOfMonth(31).withMonthOfYear(12).getMillis, 50, new DateTime().getMillis)

  def insertPrices(prices : Set[PriceForRange] = Set(defaultPrice)) = {

    val dataSource = configured[DefaultDB]
    val collection = dataSource(priceForRange).asInstanceOf[BSONCollection]

    prices.foreach(collection.insert(_))

  }
}
