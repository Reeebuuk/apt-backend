package hr.com.blanka.apartments

import reactivemongo.api._
import scala.concurrent.ExecutionContext.Implicits.global

trait MongoDbConfiguration extends Configuration {

  configure {
    val driver = new MongoDriver
    val connection = driver.connection(List("localhost"))
    connection("test")
  }
}
