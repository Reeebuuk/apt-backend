package hr.com.blanka.apartments

import akka.actor.ActorRef
import com.typesafe.config.ConfigFactory
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.{MongoCmdOptionsBuilder, MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.scalatest._

object IntegrationConf {

  def config(port: Int, className: String) = ConfigFactory.parseString(
    s"""
       |akka{
       |  actor {
       |    provider = "akka.cluster.ClusterActorRefProvider"
       |  }
       |  persistence {
       |    journal.plugin = "akka-contrib-mongodb-persistence-journal"
       |    snapshot-store.plugin = "akka-contrib-mongodb-persistence-snapshot"
       |  }
       |  remote {
       |    log-remote-lifecycle-events = off
       |    netty.tcp {
       |      hostname = "127.0.0.1"
       |      port = 8999
       |    }
       |  }
       |
       |  cluster {
       |    seed-nodes = [
       |      "akka.tcp://hr-com-blanka-apartments-$className@127.0.0.1:8999"
       |    ]
       |  }
       |
       |  extensions=["akka.cluster.metrics.ClusterMetricsExtension"]
       |  contrib.persistence.mongodb.mongo {
       |    mongouri = "mongodb://localhost:$port"
       |    journal-collection = "my_persistent_journal"
       |    journal-index = "my_journal_index"
       |    snaps-collection = "my_persistent_snapshots"
       |    snaps-index = "my_snaps_index"
       |    journal-write-concern = "Acknowledged"
       |  }
       |}
    """.stripMargin)

  lazy val freePort = Network.getFreeServerPort
}

trait IntegrationTestMongoDbSupport extends FlatSpec with BeforeAndAfterAll {

  import IntegrationConf._

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

  override protected def beforeAll() = {
    mongodExe
    super.beforeAll()
  }

  override protected def afterAll() = {
    super.afterAll()
    mongod.stop()
    mongodExe.stop()
  }

}
