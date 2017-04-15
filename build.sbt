import Dependencies._
// import Resolvers._

name := "snaut"

organization := "com.github.larsjaas"
organizationName := "Lars J. Aas"
organizationHomepage := Some(url("http://larsjaas.github.com/"))

startYear := Some(2016)
description := "A tool for generating weather forecast images."

licenses += "GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html")

scalaVersion := "2.12.1"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-eNDXEHLO")

// resolvers := portfolioResolvers

libraryDependencies ++= snautDeps

