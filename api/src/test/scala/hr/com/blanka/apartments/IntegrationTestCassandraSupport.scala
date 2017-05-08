package hr.com.blanka.apartments

import java.io.IOException
import java.net.ServerSocket

import akka.actor.ActorRef
import com.typesafe.config.ConfigFactory
import org.scalatest._

import scala.concurrent.forkjoin.ThreadLocalRandom

object IntegrationConf {

  def config(port: Int, className: String) = ConfigFactory.parseString(
    s"""
       |akka{
       |  actor {
       |    provider = "akka.cluster.ClusterActorRefProvider"
       |  }
       |  persistence {
       |    persistence.journal.plugin = "cassandra-journal"
       |    persistence.snapshot-store.plugin = "cassandra-snapshot-store"
       |  }
       |  remote {
       |    log-remote-lifecycle-events = off
       |    netty.tcp {
       |      hostname = "127.0.0.1"
       |      port = 9005
       |    }
       |  }
       |
       |  cluster {
       |    seed-nodes = [
       |      "akka.tcp://hr-com-blanka-apartments-$className@127.0.0.1:9005"
       |    ]
       |  }
       |
       |  sharding {
       |    remember-entities = false
       |    journal-plugin-id = "cassandra-journal"
       |    snapshot-plugin-id = "cassandra-snapshot-store"
       |  }
       |
       |  extensions=["akka.cluster.metrics.ClusterMetricsExtension"]
       |}
    """.stripMargin)

  lazy val freePort = FreePort.nextFreePort(49152, 65535)
}

trait IntegrationTestCassandraSupport extends FlatSpec with BeforeAndAfterAll {

  import IntegrationConf._

  val freePort: Int = FreePort.nextFreePort(49152, 65535)

  lazy val host = "localhost"

  import org.cassandraunit.utils.EmbeddedCassandraServerHelper

  EmbeddedCassandraServerHelper.startEmbeddedCassandra()

  var employeeProcessor: ActorRef = _
  var benefitsView: ActorRef = _

  override protected def beforeAll() = {
    super.beforeAll()
  }

  override protected def afterAll() = {
    super.afterAll()
  }


}

object FreePort {

  def nextFreePort(from: Int, to: Int): Int = {
    var port = ThreadLocalRandom.current().nextInt(from, to)
    while (true) {
      if (isLocalPortFree(port)) {
        return port
      } else {
        port = ThreadLocalRandom.current().nextInt(from, to)
      }
    }
    port
  }

  private def isLocalPortFree(port: Int): Boolean = {
    try {
      new ServerSocket(port).close()
      true
    } catch {
      case _: IOException => false
    }
  }
}
