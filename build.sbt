enablePlugins(SbtNativePackager)
enablePlugins(JavaServerAppPackaging)

name          := "Apartments Blanka backend"

lazy val api =  project in file("api")

lazy val root =  (project in file(".")) aggregate(api) dependsOn(api)


organization  := "com.apt"

version       := "1.0.0"

scalaVersion  := "2.12.1"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

resolvers += "Sonatype releases" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

parallelExecution in Test := false

fork in run := false