package hr.com.blanka.apartments

import akka.actor.ActorRef
import com.typesafe.config.ConfigFactory
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.{MongoCmdOptionsBuilder, MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.scalatest._

object BenefitsSpec {

  def config(port: Int) = ConfigFactory.parseString(
    s"""
       |akka.persistence.journal.plugin = "akka-contrib-mongodb-persistence-journal"
       |akka.persistence.snapshot-store.plugin = "akka-contrib-mongodb-persistence-snapshot"
       |
       |akka.contrib.persistence.mongodb.mongo {
       |  mongouri = "mongodb://localhost:$port"
       |  journal-collection = "my_persistent_journal"
       |  journal-index = "my_journal_index"
       |  snaps-collection = "my_persistent_snapshots"
       |  snaps-index = "my_snaps_index"
       |  journal-write-concern = "Acknowledged"
       |}
    """.stripMargin)

  lazy val freePort = Network.getFreeServerPort
}

trait IntegrationTestMongoDbSupport extends WordSpecLike with BeforeAndAfterAll {

  import BenefitsSpec._

  lazy val version = Version.V3_2_1
  lazy val host = "localhost"
  lazy val port = freePort
  lazy val localHostIPV6 = Network.localhostIsIPv6()

  val mongodConfig =
    new MongodConfigBuilder()
      .version(version)
      .net(new Net(port, localHostIPV6))
      .cmdOptions(new MongoCmdOptionsBuilder()
        .syncDelay(1)
        .useNoPrealloc(false)
        .useSmallFiles(false)
        .useNoJournal(false)
        .enableTextSearch(true)
        .build())
      .build()

  lazy val mongodStarter = MongodStarter.getDefaultInstance
  lazy val mongod = mongodStarter.prepare(mongodConfig)
  lazy val mongodExe = mongod.start()

  var employeeProcessor: ActorRef = _
  var benefitsView: ActorRef = _

  override def beforeAll() = {
    mongodExe
    super.beforeAll()
  }

  override def afterAll() = {
    super.afterAll()
    mongod.stop()
    mongodExe.stop()
  }

}
