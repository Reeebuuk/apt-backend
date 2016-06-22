name := "Api"

resolvers += "Sonatype releases" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

scalaVersion  := "2.11.8"

libraryDependencies ++= {
  val akkaV              = "2.4.7"
  val scalaTestV         = "2.2.6"
  val nscalaV            = "2.12.0"
  val kamonV             = "0.5.2"
  val scalaLoggingV      = "3.1.0"
  val json4sV            = "3.3.0"
  val scalacticV         = "2.2.6"
  val cassandraPersistV  = "0.11"
  val levelDbV  = "0.7"
  val levelDbJniV  = "1.8"

  Seq(
    "com.typesafe.akka" %% "akka-contrib" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-core" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaV,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
    "org.json4s" %% "json4s-jackson" % json4sV,
    "com.github.nscala-time" %% "nscala-time" % nscalaV,
    "org.scalactic" %% "scalactic" % scalacticV,

    "org.iq80.leveldb" % "leveldb" % levelDbV,
    "org.fusesource.leveldbjni" % "leveldbjni-all" % levelDbJniV,

    "com.typesafe.akka" %% "akka-persistence-cassandra" % cassandraPersistV,

    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV % "test"

  )
}