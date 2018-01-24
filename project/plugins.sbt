logLevel := Level.Warn
resolvers += Classpaths.sbtPluginReleases
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M9")
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.4")

