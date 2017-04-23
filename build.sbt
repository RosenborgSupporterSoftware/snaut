import Dependencies._

name := "snaut"

organization := "no.rbkss.snaut"
organizationName := "Lars J. Aas"
organizationHomepage := Some(url("http://rosenborgsupportersoftware.github.io/"))

startYear := Some(2016)
description := "A tool for generating weather forecast images."

licenses += "GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html")

scalaVersion := "2.12.2"

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-eNDXEHLO")

libraryDependencies ++= snautDeps
