name := "snaut"
// version := "0.0.2"
scalaVersion := "2.12.1"

libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.25",
                            "org.slf4j" % "slf4j-simple" % "1.7.25",
                            "commons-io" % "commons-io" % "2.5",
                            "ch.qos.logback" % "logback-core" % "1.2.3",
                            "org.scala-lang" % "scala-xml" % "2.11.0-M4",
                            "org.clapper" %% "grizzled-slf4j" % "1.3.0",
                            "org.joda" % "joda-convert" % "1.8.1",
                            "joda-time" % "joda-time" % "2.9.9",
                            "com.typesafe" % "config" % "1.3.1",
                            "org.scalactic" %% "scalactic" % "3.0.1" % "test",
                            "org.scalatest" %% "scalatest" % "3.0.1" % "test")

// "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-eNDXEHLO")
