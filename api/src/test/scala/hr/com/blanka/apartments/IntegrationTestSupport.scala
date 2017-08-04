package hr.com.blanka.apartments

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.ThreadLocalRandom

import com.typesafe.config.{ Config, ConfigFactory }

object IntegrationConf {

  def config(port: Int, className: String): Config = ConfigFactory.parseString(s"""
       |akka{
       |  actor {
       |    provider = "akka.cluster.ClusterActorRefProvider"
       |  }
       |  persistence {
       |    journal.plugin = "akka.persistence.journal.leveldb"
       |    snapshot-store.plugin = "akka.persistence.journal.leveldb"
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
       |    journal-plugin-id = "inmemory-journal"
       |    snapshot-plugin-id = "inmemory-snapshot-store"
       |  }
       |
       |  extensions=["akka.cluster.metrics.ClusterMetricsExtension"]
       |}
       |inmemory-snapshot-store {
       |  class = "akka.persistence.inmemory.snapshot.InMemorySnapshotStore"
       |  ask-timeout = "10s"
       |}
       |
       |inmemory-read-journal {
       |  refresh-interval = "100ms"
       |  max-buffer-size = "100"
       |}
    """.stripMargin)

  lazy val freePort: Int = FreePort.nextFreePort(49152, 65535)
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
