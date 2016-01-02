package hr.com.blanka.apartments.utils

import com.typesafe.config.ConfigFactory

trait AppConfig {
  private val config = ConfigFactory.load()
  lazy val httpConfig = config.getConfig("http")
  lazy val pricingConfig = ConfigFactory.load("pricing.conf")

  lazy val httpInterface = httpConfig.getString("interface")
  lazy val httpPort = httpConfig.getInt("port")
}
