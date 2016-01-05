package hr.com.blanka.apartments

import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodProcess, MongodStarter}
import hr.com.blanka.apartments.http.BaseService
import org.scalatest._
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection

trait IntegrationTestMongoDbSupport extends WordSpec with BeforeAndAfterAll with TestMongoDbConfiguration {

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

}
