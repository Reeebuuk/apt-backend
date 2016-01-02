enablePlugins(JavaAppPackaging)

name          := "crudapi"

organization  := "com.apt"

version       := "1.0.0"

scalaVersion  := "2.11.7"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

resolvers += "Sonatype releases" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= {
  val akkaStreamV        = "2.0.1"
  val akkaV              = "2.4.1"
  val scalaTestV         = "2.2.4"
  val slf4jVersion       = "1.6.4"
  val rMongoVersion      = "0.11.7"
  val inMemMongoVersion  = "1.50.0"
  val nscalaVersion      = "2.2.0"
  val kamonVersion       = "0.5.2"

  Seq(
    "com.typesafe.akka" %% "akka-contrib" % akkaV,
    "com.typesafe.akka" %% "akka-remote" % akkaV,
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
    "com.typesafe.scala-logging" %% "scala-logging" % "3.0.0",
    "org.slf4j" % "slf4j-nop" % slf4jVersion,
    "org.json4s" %% "json4s-native" % "3.2.11",
    "org.reactivemongo" %% "reactivemongo" % rMongoVersion,
    "org.reactivemongo" %% "reactivemongo-bson-macros" % rMongoVersion,
    "com.github.nscala-time" %% "nscala-time" % nscalaVersion,
    "com.typesafe.akka" %% "akka-typed-experimental" % akkaV,
    "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % inMemMongoVersion % "test",
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV % "test",

    "io.kamon" %% "kamon-core" % kamonVersion,
    "io.kamon" %% "kamon-scala" % kamonVersion,
    "io.kamon" %% "kamon-akka" % kamonVersion,
    "io.kamon" %% "kamon-system-metrics" % kamonVersion,
    "io.kamon" %% "kamon-annotation" % kamonVersion
  )
}


initialCommands := """|import akka.actor._
                     |import akka.pattern._
                     |import akka.util._
                     |import scala.concurrent._
                     |import scala.concurrent.duration._""".stripMargin

parallelExecution in Test := false
