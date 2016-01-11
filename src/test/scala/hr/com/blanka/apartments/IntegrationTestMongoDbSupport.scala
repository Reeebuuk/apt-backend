package hr.com.blanka.apartments

import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodProcess, MongodStarter}
import org.scalatest._

trait IntegrationTestMongoDbSupport extends WordSpecLike with BeforeAndAfterAll with TestMongoDbConfiguration {

  lazy val runtime : MongodStarter = MongodStarter.getDefaultInstance
  lazy val mongoExe : MongodExecutable = runtime.prepare(
    new MongodConfigBuilder()
      .version(version)
      .net(new Net(url, port, false))
      .build())

  lazy val mongod: MongodProcess = mongoExe.start()

  override protected def beforeAll(): Unit = {
    mongod
    super.beforeAll()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    mongod.stop()
    mongoExe.stop()
  }

}
