package com.example.crudapi.utils

import com.typesafe.config.ConfigFactory

trait AppConfig {
  private val config = ConfigFactory.load()
  val httpConfig = config.getConfig("http")
  val pricingConfig = ConfigFactory.load("pricing.conf")

  val httpInterface = httpConfig.getString("interface")
  val httpPort = httpConfig.getInt("port")
}
