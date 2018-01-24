enablePlugins(JavaAppPackaging, DockerPlugin)
import com.typesafe.sbt.packager.docker._

dockerBaseImage := "openjdk:alpine"
dockerEntrypoint := {
  val nameLowerCase = name.value.toLowerCase
  Seq("/bin/bash", "-c",
      s" bin/$nameLowerCase -Dconfig.file=/opt/docker/application.conf"
  )
}
dockerCommands := {
  dockerCommands.value.take(1) ++
    Seq(ExecCmd("RUN", "apk", "add", "--update", "libstdc++", "bash")) ++
    dockerCommands.value.drop(1)
}

name := "sales-sample-app"
organization := "com.maurogonzalez"
version := "v1.0.0"
scalaVersion := "2.12.2"

lazy val showName = taskKey[Unit]("Show name")
showName := {
  println(s"${name.value.toLowerCase}-${version.value}")
}


lazy val showVersion = taskKey[Unit]("Show version")
showVersion := {
  println(version.value)
}

scalacOptions := Seq("-unchecked", "-feature","-deprecation", "-encoding", "utf8")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

resolvers += "jboss-releases" at "https://repository.jboss.org/nexus/content/repositories/releases"
resolvers += "jboss-jsr94" at "http://repository.jboss.org/nexus/content/groups/public-jboss"
resolvers += Resolver.bintrayRepo("hseeberger", "maven")

libraryDependencies ++= {
  val akkaV       = "2.5.2"
  val akkaHttpV   = "10.0.7"
  val scalaTestV  = "3.0.1"
  val slf4jV      = "1.7.5"
  val circeV      = "0.8.0"
  Seq(
    "org.scalaz"                   %% "scalaz-core"              % "7.2.18",
    "com.typesafe.akka"            %% "akka-actor"               % akkaV,
    "com.typesafe.akka"            %% "akka-stream"              % akkaV,
    "com.typesafe.akka"            %% "akka-testkit"             % akkaV,
    "com.typesafe.akka"            %% "akka-slf4j"               % akkaV,
    "com.typesafe.akka"            %% "akka-http"                % akkaHttpV,
    "com.typesafe.akka"            %% "akka-http-spray-json"     % akkaHttpV,
    "com.typesafe.akka"            %% "akka-http-testkit"        % akkaHttpV,
    "ch.megard"                    %% "akka-http-cors"           % "0.1.11",

    "de.heikoseeberger"            %% "akka-http-circe"          % "1.16.0",
    "io.circe"                     %% "circe-core"               % circeV,
    "io.circe"                     %% "circe-generic"            % circeV,
    "io.circe"                     %% "circe-parser"             % circeV,

    "ch.qos.logback"               %  "logback-classic"          % "1.1.6",
    "net.logstash.logback"         %  "logstash-logback-encoder" % "4.7",

    "org.slf4j"                    %  "slf4j-api"                % slf4jV,

    "org.scalatest"                %% "scalatest"                % scalaTestV % "test",
    "org.scalamock"                %% "scalamock-scalatest-support" % "3.4.2" % "test"
  )
}

libraryDependencies += "com.github.swagger-akka-http" % "swagger-akka-http_2.12" % "0.9.2"

mappings in Universal += file("src/main/resources/application.conf") -> "application.conf"

Revolver.settings
