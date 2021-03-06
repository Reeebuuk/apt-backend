scalaVersion := "2.12.4"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

lazy val api =
  project
    .settings(settings)
    .settings(
      libraryDependencies ++= {
        val akkaV                     = "2.5.11"
        val akkaHttpV                 = "10.1.0"
        val scalaTestV                = "3.0.4"
        val scalacticV                = "3.0.4"
        val akkaPersistanceCassandraV = "0.59"
        val akkaHttpPlayJsonSupportV  = "1.18.0"
        val playJsonV                 = "2.6.7"
        val logbackClassicV           = "1.2.3"
        val scalaLoggingV             = "3.7.2"
        val akkaHttpCorsV             = "0.2.2"
        val javaMailV                 = "1.6.0"

        Seq(
          "com.typesafe.akka"          %% "akka-contrib"                        % akkaV,
          "com.typesafe.akka"          %% "akka-stream"                         % akkaV,
          "com.typesafe.akka"          %% "akka-cluster"                        % akkaV,
          "com.typesafe.akka"          %% "akka-cluster-sharding"               % akkaV,
          "com.typesafe.akka"          %% "akka-persistence-query"              % akkaV,
          "com.typesafe.akka"          %% "akka-http-core"                      % akkaHttpV,
          "com.typesafe.akka"          %% "akka-http-spray-json"                % akkaHttpV,
          "com.typesafe.akka"          %% "akka-persistence-cassandra"          % akkaPersistanceCassandraV,
          "de.heikoseeberger"          %% "akka-http-play-json"                 % akkaHttpPlayJsonSupportV,
          "com.typesafe.play"          %% "play-json"                           % playJsonV,
          "org.scalactic"              %% "scalactic"                           % scalacticV,
          "ch.qos.logback"             % "logback-classic"                      % logbackClassicV,
          "com.typesafe.scala-logging" %% "scala-logging"                       % scalaLoggingV,
          "ch.megard"                  %% "akka-http-cors"                      % akkaHttpCorsV,
          "com.sun.mail"               % "javax.mail"                           % javaMailV,
          "com.typesafe.akka"          %% "akka-persistence-cassandra-launcher" % akkaPersistanceCassandraV % Test,
          "org.scalatest"              %% "scalatest"                           % scalaTestV % Test,
          "com.typesafe.akka"          %% "akka-http-testkit"                   % akkaHttpV % Test
        )
      }
    )

lazy val root = (project in file(".")) aggregate api dependsOn api

lazy val settings =
commonSettings ++
scalafmtSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.12.3",
    organization := "hr.com.apartments-blanka",
    organizationName := "Krunoslav Uzelac",
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding",
      "UTF-8"
    ),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    shellPrompt in ThisBuild := { state =>
      val project = Project.extract(state).currentRef.project
      s"[$project]> "
    }
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.3.0"
  )

parallelExecution in Test := false

fork in run := false
