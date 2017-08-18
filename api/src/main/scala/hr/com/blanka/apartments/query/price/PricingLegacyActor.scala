package hr.com.blanka.apartments.query.price

import akka.actor.{ Actor, Props }
import com.typesafe.config.{ Config, ConfigFactory }
import hr.com.blanka.apartments.ValueClasses.UnitId
import hr.com.blanka.apartments.common.DayMonth
import org.scalactic.Good

import scala.collection.JavaConverters._

object PricingLegacyActor {
  def apply() = Props(classOf[PricingLegacyActor])
}

class PricingLegacyActor extends Actor {

  private val pricingConf: Config = ConfigFactory.load("pricing")

  private val pricingList: List[Config]  = pricingConf.getConfigList("dateRange").asScala.toList
  private val apartmentIds: List[Config] = pricingConf.getConfigList("unitId").asScala.toList

  lazy val pricingRangeList: List[PricePerPeriod] =
    pricingList.toArray.foldLeft(List[PricePerPeriod]()) {
      (result: List[PricePerPeriod], any: AnyRef) =>
        val config = any.asInstanceOf[Config]

        result :+ PricePerPeriod(
          DayMonth(config.getInt("fromDay"), config.getInt("fromMonth")),
          DayMonth(config.getInt("toDay"), config.getInt("toMonth")),
          apartmentIds.toArray.foldLeft(Map[UnitId, Int]()) {
            (mapResult: Map[UnitId, Int], idObject: AnyRef) =>
              val idConfig = idObject.asInstanceOf[Config]

              mapResult ++ Map(
                UnitId(idConfig.getInt("id")) -> config.getInt(idConfig.getString("name"))
              ) ++ mapResult
          }
        )
    }

  override def receive: Receive = {
    case _: LegacyLookupAllPrices =>
      sender() ! Good(pricingRangeList)
  }
}
