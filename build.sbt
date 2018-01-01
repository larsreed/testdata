scalaVersion := "2.12.4"

scalacOptions += "-feature"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

organization := "no.netcompany"

name := "testdatagen"

version := "0.2-SNAPSHOT"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.9.9",
  "org.joda" % "joda-convert" % "1.9.2",
  "com.github.nscala-time" %% "nscala-time" % "2.18.0",
  "org.scalaz.stream" %% "scalaz-stream" % "0.8.6"
)

libraryDependencies ++= Seq( // test
  "junit" % "junit" % "4.12" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "org.hamcrest" % "hamcrest-core" % "1.3" % "test",
  "org.specs2" %% "specs2-core" % "3.8.9" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test"
)

// org.scalastyle.sbt.ScalastylePlugin.Settings