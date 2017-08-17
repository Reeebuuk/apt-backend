package hr.com.blanka.apartments

import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.ThreadLocalRandom

import com.typesafe.config.{ Config, ConfigFactory }

object IntegrationConf {

  def config(className: String): Config =
    ConfigFactory.parseString(s"""
 |akka {
 |  loglevel = DEBUG
 |}
 |http {
 |  interface = "0.0.0.0"
 |  port = 9000
 |}
 |
 |akka {
 |  actor {
 |    serializers {
 |      myown = "hr.com.blanka.apartments.AkkaPersistenceSerializer"
 |    }
 |    serialization-bindings {
 |      "java.io.Serializable" = none
 |      "hr.com.blanka.apartments.command.booking.EnquirySaved" = myown
 |      "hr.com.blanka.apartments.command.booking.EnquiryBooked" = myown
 |      "hr.com.blanka.apartments.command.booking.NewBookingIdAssigned" = myown
 |      "hr.com.blanka.apartments.command.price.DailyPriceSaved" = myown
 |    }
 |    provider = "akka.cluster.ClusterActorRefProvider"
 |  }
 |
 |  persistence {
 |    journal.plugin = "cassandra-journal"
 |    snapshot-store.plugin = "cassandra-snapshot-store"
 |  }
 |
 |  remote {
 |    log-remote-lifecycle-events = off
 |    netty.tcp {
 |      hostname = "127.0.0.1"
 |      port = 8998
 |    }
 |  }
 |
 |  cluster {
 |    seed-nodes = [
 |      "akka.tcp://hr-com-blanka-apartments-$className@127.0.0.1:8998"
 |    ]
 |
 |    sharding {
 |      remember-entities = false
 |      journal-plugin-id = "cassandra-journal"
 |      snapshot-plugin-id = "cassandra-snapshot-store"
 |    }
 |  }
 |
 |}
 |
 |cassandra-journal {
 |  contact-points = ["localhost"]
 |  port = 9043
 |  keyspace = "booking_engine_journal"
 |  class = "akka.persistence.cassandra.journal.CassandraJournal"
 |  cassandra-2x-compat = off
 |  enable-events-by-tag-query = on
 |}
 |
 |cassandra-snapshot-store {
 |  class = "akka.persistence.cassandra.snapshot.CassandraSnapshotStore"
 |  contact-points = ["localhost"]
 |  port = 9043
 |  session-provider = akka.persistence.cassandra.ConfigSessionProvider
 |  keyspace = "booking_engine_journal"
 |  keyspace-autocreate = true
 |  tables-autocreate = true
 |  connect-retries = 3
 |  connect-retry-delay = 1s
 |  reconnect-max-delay = 30s
 |  table = "snapshots"
 |  table-compaction-strategy {
 |    class = "SizeTieredCompactionStrategy"
 |  }
 |  config-table = "config"
 |  metadata-table = "metadata"
 |  replication-strategy = "SimpleStrategy"
 |  replication-factor = 1
 |}
 |
 |cassandra-query-journal {
 |  class = "akka.persistence.cassandra.query.CassandraReadJournalProvider"
 |  write-plugin = "cassandra-journal"
 |  refresh-interval = 50ms
 |  max-buffer-size = 500
 |  max-result-size-query = 250
 |  read-consistency = "QUORUM"
 |  plugin-dispatcher = "cassandra-plugin-default-dispatcher"
 |}
 |
 |cassandra-plugin-default-dispatcher {
 |  type = Dispatcher
 |  executor = "fork-join-executor"
 |  fork-join-executor {
 |    parallelism-min = 8
 |    parallelism-factor = 1.0
 |    parallelism-max = 16
 |  }
 |}
 |
 |cassandra-plugin-blocking-dispatcher {
 |  type = Dispatcher
 |  executor = "thread-pool-executor"
 |  thread-pool-executor {
 |    fixed-pool-size = 16
 |  }
 |  throughput = 1
 |}""".stripMargin)

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

  private def isLocalPortFree(port: Int): Boolean =
    try {
      new ServerSocket(port).close()
      true
    } catch {
      case _: IOException => false
    }
}
