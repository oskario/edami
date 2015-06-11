name := """edami"""

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots")
)
