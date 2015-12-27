package com.example

import akka.event.{LoggingAdapter, NoLogging}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.example.crudapi.http.BaseService
import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodProcess, MongodStarter}
import org.scalatest._
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection

trait MongoDbSupport extends WordSpec with BaseService with BeforeAndAfterAll {

  def url = "localhost"
  def port = 6667
  def version = Version.V3_0_5
  def dbName = "test_db"

  lazy val runtime : MongodStarter = MongodStarter.getDefaultInstance
  lazy val mongoExe : MongodExecutable = runtime.prepare(
    new MongodConfigBuilder()
      .version(version)
      .net(new Net(url, port,false))
      .build())

  lazy val mongod: MongodProcess = mongoExe.start()

  override def beforeAll(): Unit = {
    mongod
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    mongod.stop()
    mongoExe.stop()
  }

  def connect() : BSONCollection = {
    // gets an instance of the driver
    // (creates an actor system)
    val driver = new MongoDriver
    val connection = driver.connection(List(s"$url:${port.toString}"))

    // Gets a reference to the database "plugin"
    val database : DefaultDB= connection(dbName)

    // Gets a reference to the collection "acoll"
    // By default, you get a BSONCollection.
    database("booking")
  }

  val bookingsCollection = connect()

}
