import sbt._
import Keys._

object Dependencies {
    lazy val akkaVersion = "2.5.0"
    lazy val scalaTestVersion = "3.0.3"
    lazy val logbackVersion = "1.2.3"

    val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.25"
    val logbackCore = "ch.qos.logback" % "logback-core" % logbackVersion
    val logbackClassic = "ch.qos.logback" % "logback-classic" % logbackVersion
    val grizzledSlf4j = "org.clapper" %% "grizzled-slf4j" % "1.3.0"
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"

    val commonsIo = "commons-io" % "commons-io" % "2.5"
    val jodaConvert = "org.joda" % "joda-convert" % "1.8.1"
    val jodaTime = "joda-time" % "joda-time" % "2.9.9"
    val config = "com.typesafe" % "config" % "1.3.1"
    val cron4s = "com.github.alonsodomin.cron4s" %% "cron4s-core" % "0.4.0"
    val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.6"

    val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
    val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
    val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
    val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.0.5"

    val scalactic = "org.scalactic" %% "scalactic" % scalaTestVersion % "test"
    val scalatest = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"

    val snautDeps = Seq(slf4jApi, logbackCore, logbackClassic, scalaXml, scalaLogging,
        jodaConvert, jodaTime, config, commonsIo,
        grizzledSlf4j, cron4s, akkaActor, akkaStream, akkaSlf4j, akkaHttp, scalactic, scalatest)
}
