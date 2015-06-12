name := """edami"""

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.scalanlp" %% "breeze" % "0.11.2",
  "org.scalanlp" %% "breeze-viz" % "0.11.2"
  // greatly improves performance, but unfortunately doesn't work :/
  // "org.scalanlp" %% "breeze-natives" % "0.11.2"
)

resolvers ++= Seq(
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)
