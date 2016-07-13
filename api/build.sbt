name := "Api"

resolvers += "Sonatype releases" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

scalaVersion  := "2.11.8"

libraryDependencies ++= {
  val akkaV              = "2.4.7"
  val scalaTestV         = "2.2.6"
  val nscalaV            = "2.12.0"
  val scalaLoggingV      = "3.1.0"
  val json4sV            = "3.3.0"
  val scalacticV         = "2.2.6"
  val scullxbonesMongoV  = "1.2.5"
  val mongoCasbahV       = "3.1.1"
  val embeddedMongoV     = "1.50.5"

  Seq(
    "com.github.scullxbones" %% "akka-persistence-mongo-casbah" % scullxbonesMongoV,
    "org.mongodb" %% "casbah" % mongoCasbahV,
    "com.typesafe.akka" %% "akka-contrib" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-core" % akkaV,
    "com.typesafe.akka" %% "akka-cluster" % akkaV,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
    "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaV,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingV,
    "org.json4s" %% "json4s-jackson" % json4sV,
    "org.scalactic" %% "scalactic" % scalacticV,

    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-http-testkit" % akkaV % "test",
    "de.flapdoodle.embed" % "de.flapdoodle.embed.mongo" % embeddedMongoV % "test"
  )
}