enablePlugins(JavaAppPackaging)

name          := "Apartments Blanka backend"

lazy val api =  project in file("api")

lazy val root =  (project in file(".")) aggregate(api) dependsOn(api)


organization  := "com.apt"

version       := "1.0.0"

scalaVersion  := "2.12.3"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

parallelExecution in Test := false

fork in run := false