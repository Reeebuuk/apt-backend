package hr.com.blanka.apartments.utils

import com.typesafe.config.Config

import scala.collection.JavaConverters._

case class PricePerPeriod(from: Long, to: Long, appPrice: Map[Int, Int])

case class PricingConfig(pricings : List[PricePerPeriod])

object PricingConfig extends DateUtils{

  def apply(config: Config) = {
    new PricingConfig(pricingForApartment(
      config.getConfigList("dateRange").asScala.toList,
      config.getConfigList("apartmentId").asScala.toList))
  }

  private def pricingForApartment(pricingList: List[_ <: Config], apartmentIds: List[_ <: Config]): List[PricePerPeriod] = {
    pricingList.foldLeft(List[PricePerPeriod]()) {
      (result: List[PricePerPeriod], config: Config) =>

        result :+ new PricePerPeriod(
          calculateDate(config.getInt("fromDay"), config.getInt("fromMonth")),
          calculateDate(config.getInt("toDay"), config.getInt("toMonth")),
          apartmentIds.foldLeft(Map[Int,Int]()) {
            (mapResult: Map[Int, Int], idConfig: Config) =>
              mapResult ++ Map(idConfig.getInt("id") -> config.getInt(idConfig.getString("name"))) ++ mapResult
          }
        )
    }
  }
}
