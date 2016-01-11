package hr.com.blanka.apartments

import de.flapdoodle.embed.mongo.distribution.Version
import reactivemongo.api._

import scala.concurrent.ExecutionContext.Implicits.global

trait TestMongoDbConfiguration extends Configuration {

  val url = "localhost"
  val port = 27000
  val version = Version.V3_2_0
  val dbName = "test"

  configure {
    val driver = new MongoDriver
    val connection = driver.connection(List(s"$url:$port"))
    connection(dbName)
  }
}
