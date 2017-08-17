scalaVersion := "2.12.3"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

lazy val api =
  project
    .settings(settings)
    .settings(
      libraryDependencies ++= {
        val akkaV                     = "2.5.4"
        val akkaHttpTestV             = "10.0.9"
        val scalaTestV                = "3.0.3"
        val scalacticV                = "3.0.3"
        val akkaPersistanceCassandraV = "0.54"
        val akkaHttpPlayJsonSupportV  = "1.17.0"
        val playJsonV                 = "2.6.2"
        val logbackClassicV           = "1.2.3"
        val scalaLoggingV             = "3.7.2"
        val akkaHttpCorsV             = "0.2.1"

        Seq(
          "com.typesafe.akka"          %% "akka-contrib"                        % akkaV,
          "com.typesafe.akka"          %% "akka-stream"                         % akkaV,
          "com.typesafe.akka"          %% "akka-cluster"                        % akkaV,
          "com.typesafe.akka"          %% "akka-cluster-sharding"               % akkaV,
          "com.typesafe.akka"          %% "akka-persistence-query"              % akkaV,
          "com.typesafe.akka"          %% "akka-http-core"                      % akkaHttpTestV,
          "com.typesafe.akka"          %% "akka-http-spray-json"                % akkaHttpTestV,
          "com.typesafe.akka"          %% "akka-persistence-cassandra"          % akkaPersistanceCassandraV,
          "de.heikoseeberger"          %% "akka-http-play-json"                 % akkaHttpPlayJsonSupportV,
          "com.typesafe.play"          %% "play-json"                           % playJsonV,
          "org.scalactic"              %% "scalactic"                           % scalacticV,
          "ch.qos.logback"             % "logback-classic"                      % logbackClassicV,
          "com.typesafe.scala-logging" %% "scala-logging"                       % scalaLoggingV,
          "ch.megard"                  %% "akka-http-cors"                      % akkaHttpCorsV,
          "com.typesafe.akka"          %% "akka-persistence-cassandra-launcher" % akkaPersistanceCassandraV % Test,
          "org.scalatest"              %% "scalatest"                           % scalaTestV % Test,
          "com.typesafe.akka"          %% "akka-http-testkit"                   % akkaHttpTestV % Test
        )
      }
    )

lazy val root = (project in file(".")) aggregate api dependsOn api

lazy val settings =
commonSettings ++
gitSettings ++
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

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.1.0"
  )

parallelExecution in Test := false

fork in run := false
