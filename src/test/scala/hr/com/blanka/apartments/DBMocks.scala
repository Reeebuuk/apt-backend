package hr.com.blanka.apartments

import hr.com.blanka.apartments.model.PriceForRange
import org.joda.time.DateTime
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.Macros

trait DBMocks { self: Configured =>

  implicit val priceForRangeFormat = Macros.handler[PriceForRange]

  def insertPrices()= {

    val dataSource = configured[DefaultDB]
    val collection = dataSource(priceForRange).asInstanceOf[BSONCollection]

    collection.insert(PriceForRange(1, new DateTime().st))
  }
}
