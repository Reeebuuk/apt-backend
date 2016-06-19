enablePlugins(SbtNativePackager)
enablePlugins(JavaServerAppPackaging)

name          := "Apartments Blanka backend"

organization  := "com.apt"

version       := "1.0.0"

scalaVersion  := "2.11.8"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

resolvers += "Sonatype releases" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

parallelExecution in Test := false

fork in run := false

kamon.aspectj.sbt.AspectjRunner.testSettings