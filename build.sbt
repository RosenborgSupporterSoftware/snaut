name := "snaut"
version := "0.0.1"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.21",
                            "org.slf4j" % "slf4j-simple" % "1.7.21",
                            "ch.qos.logback" % "logback-core" % "1.1.7",
                            "org.scala-lang" % "scala-xml" % "2.11.0-M4",
                            "org.clapper" %% "grizzled-slf4j" % "1.1.0",
                            "org.joda" % "joda-convert" % "1.8.1",
                            "joda-time" % "joda-time" % "2.9.4",
                            "com.typesafe" % "config" % "1.3.0",
                            "org.scalactic" %% "scalactic" % "3.0.0" % "test",
                            "org.scalatest" %% "scalatest" % "3.0.0" % "test")

// "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
