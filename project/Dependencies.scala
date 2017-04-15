import sbt._
import Keys._

object Dependencies {
    //lazy val akkaVersion = "2.4.17"
    lazy val scalaTestVersion = "3.0.1"
    lazy val logbackVersion = "1.2.3"

    val slf4jApi = "org.slf4j" % "slf4j-api" % "1.7.25"
    val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.7.25"
    val commonsIo = "commons-io" % "commons-io" % "2.5"
    val logbackCore = "ch.qos.logback" % "logback-core" % logbackVersion
    val jodaConvert = "org.joda" % "joda-convert" % "1.8.1"
    val jodaTime = "joda-time" % "joda-time" % "2.9.9"
    val config = "com.typesafe" % "config" % "1.3.1"

    val scalaXml = "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
    val grizzledSlf4j = "org.clapper" %% "grizzled-slf4j" % "1.3.0"
    val scalactic = "org.scalactic" %% "scalactic" % scalaTestVersion % "test"
    val scalatest = "org.scalatest" %% "scalatest" % scalaTestVersion % "test"

    // val cron4s = "com.github.alonsodomin.cron4s" %% "cron4s-core" % "0.3.2"
    // "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"

    val snautDeps = Seq(slf4jApi, slf4jSimple, commonsIo, logbackCore, scalaXml, jodaConvert, jodaTime, config,
        grizzledSlf4j, scalactic, scalatest)
}
