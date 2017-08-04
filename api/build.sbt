name := "Api"

scalaVersion  := "2.12.3"

libraryDependencies ++= {
  val akkaV = "2.5.3"
  val akkaHttpTestV = "10.0.9"
  val scalaTestV = "3.0.3"
  val json4sV = "3.5.3"
  val scalacticV = "3.0.3"
  val akkaPersistanceCassandraV = "0.54"
  val cassandraUnitV = "3.1.3.2"
  val leveldbV = "0.9"
  val leveldbJniV = "1.8"

  Seq(
    "com.typesafe.akka" %% "akka-contrib" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaV,
    "com.typesafe.akka" %% "akka-persistence-query" % akkaV,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpTestV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpTestV,
    "com.typesafe.akka" %% "akka-persistence-cassandra" % akkaPersistanceCassandraV,
    "org.json4s" %% "json4s-jackson" % json4sV,
    "org.scalactic" %% "scalactic" % scalacticV,
    "org.cassandraunit" % "cassandra-unit" % cassandraUnitV % "test",
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpTestV % "test",
    "org.iq80.leveldb" % "leveldb" % leveldbV % "test",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % leveldbJniV % "test"
  )
}
