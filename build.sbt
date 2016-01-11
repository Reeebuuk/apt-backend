enablePlugins(JavaServerAppPackaging)

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
  val slf4jV             = "1.6.4"
  val rMongoV            = "0.11.9"
  val inMemMongoV        = "1.50.2"
  val nscalaV            = "2.2.0"
  val kamonV             = "0.5.2"
  val scalaLoggingV      = "3.0.0"
  val json4sV            = "3.2.11"
  val scalacticV         = "2.2.6"

  Seq(
    "com.typesafe.akka" %% "akka-contrib" % akkaV,
    "com.typesafe.akka" %% "akka-remote" % akkaV,
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
    "org.slf4j" % "slf4j-nop" % slf4jV,
    "org.json4s" %% "json4s-native" % json4sV,
    "org.reactivemongo" %% "reactivemongo" % rMongoV,
    "org.reactivemongo" %% "reactivemongo-bson-macros" % rMongoV,
    "com.github.nscala-time" %% "nscala-time" % nscalaV,
    "com.typesafe.akka" %% "akka-typed-experimental" % akkaV,
    "org.scalactic" %% "scalactic" % scalacticV,
  
    "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % inMemMongoV % "test",
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV % "test",

    "io.kamon" %% "kamon-core" % kamonV,
    "io.kamon" %% "kamon-scala" % kamonV,
    "io.kamon" %% "kamon-akka" % kamonV,
    "io.kamon" %% "kamon-system-metrics" % kamonV,
    "io.kamon" %% "kamon-annotation" % kamonV,
    "io.kamon" %% "kamon-statsd" % kamonV,
    "io.kamon" %% "kamon-log-reporter" % kamonV
  )
}


initialCommands := """|import akka.actor._
                     |import akka.pattern._
                     |import akka.util._
                     |import scala.concurrent._
                     |import scala.concurrent.duration._""".stripMargin

parallelExecution in Test := false

fork in run := false

kamon.aspectj.sbt.AspectjRunner.testSettings