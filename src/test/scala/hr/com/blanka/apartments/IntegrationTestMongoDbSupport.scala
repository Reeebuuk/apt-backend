package hr.com.blanka.apartments

import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodProcess, MongodStarter}
import org.scalatest._

trait IntegrationTestMongoDbSupport extends WordSpecLike with BeforeAndAfterAll with BeforeAndAfter
with TestMongoDbConfiguration {

  lazy val runtime : MongodStarter = MongodStarter.getDefaultInstance
  lazy val mongoExe : MongodExecutable = runtime.prepare(
    new MongodConfigBuilder()
      .version(version)
      .net(new Net(httpInterface, httpPort, false))
      .build())

  lazy val mongod: MongodProcess = mongoExe.start()

  override def beforeAll {
    mongod
    super.beforeAll()
  }

  override def afterAll {
    super.afterAll()
    mongod.stop()
    mongoExe.stop()
  }

}
