logLevel := Level.Warn
resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

addSbtPlugin("org.scalariform" % "sbt-scalariform" % "1.6.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.1")

addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")

addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.2")