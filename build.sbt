scalaVersion := "2.9.2"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

organization := "net.kalars"

name := "testdatagen"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.1",
  "org.joda" % "joda-convert" % "1.2",
  "org.scalaj" %% "scalaj-time" % "0.6"
)

libraryDependencies ++= Seq( // test
  "junit" % "junit" % "4.10" % "test",
  "org.scalatest" %% "scalatest" % "1.8" % "test",
  "org.hamcrest" % "hamcrest-core" % "1.1" % "test",
  "org.specs2" %% "specs2" % "1.12.2" % "test",
  "org.mockito" % "mockito-all" % "1.9.0" % "test"
)

org.scalastyle.sbt.ScalastylePlugin.Settings
