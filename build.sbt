scalaVersion := "2.10.2"

scalacOptions += "-feature"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

organization := "no.mesan"

name := "testdatagen"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.4",
  "org.scalaj" % "scalaj-time_2.10.2" % "0.7"
)

libraryDependencies ++= Seq( // test
  "junit" % "junit" % "4.11" % "test",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "org.hamcrest" % "hamcrest-core" % "1.1" % "test",
  "org.specs2" %% "specs2" % "2.1.1" % "test",
  "org.mockito" % "mockito-all" % "1.9.0" % "test"
)

org.scalastyle.sbt.ScalastylePlugin.Settings
