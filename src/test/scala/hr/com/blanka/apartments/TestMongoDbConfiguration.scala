package hr.com.blanka.apartments

import de.flapdoodle.embed.mongo.distribution.Version
import hr.com.blanka.apartments.utils.AppConfig
import reactivemongo.api._

import scala.concurrent.ExecutionContext.Implicits.global

trait TestMongoDbConfiguration extends Configuration with AppConfig{

  val version = Version.V3_2_0
  val dbName = "test"

  configure {
    val driver = new MongoDriver
    val connection = driver.connection(List(s"$httpInterface:$httpPort"))
    connection(dbName)
  }
}
