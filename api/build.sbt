name := "Api"

resolvers += "Sonatype releases" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

scalaVersion := "2.12.1"

libraryDependencies ++= {
  val akkaV = "2.5.1"
  val akkaHttpTestV = "10.0.6"
  val scalaTestV = "3.0.1"
  val scalaLoggingV = "3.5.0"
  val json4sV = "3.5.2"
  val scalacticV = "3.0.1"
  val akkaPersistanceCassandraV = "0.52"
  val jodaTimeV = "2.9.9"
  val cassandraUnitV = "3.1.3.2"
  val guavaV = "21.0"
  val leveldbV = "0.9"
  val leveldbJniV = "1.8"

  Seq(
    "joda-time" % "joda-time" % jodaTimeV,
//    "com.google.guava" % "guava" % guavaV,
    "com.typesafe.akka" %% "akka-contrib" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaV,
    "com.typesafe.akka" %% "akka-persistence-query" % akkaV,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpTestV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpTestV,
    "com.typesafe.akka" %% "akka-persistence-cassandra" % akkaPersistanceCassandraV,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
    "org.json4s" %% "json4s-jackson" % json4sV,
    "org.scalactic" %% "scalactic" % scalacticV,
    "org.cassandraunit" % "cassandra-unit" % cassandraUnitV % "test",
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpTestV % "test",
    "org.iq80.leveldb" % "leveldb" % leveldbV % "test",
    "org.fusesource.leveldbjni" % "leveldbjni-all" % leveldbJniV % "test"
  )
}
