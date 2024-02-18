scalaVersion := "2.12.4"

scalacOptions += "-feature"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

organization := "net.kalars"

name := "testdatagen"

version := "0.2-SNAPSHOT"

libraryDependencies ++= Seq(
  "joda-time" % "joda-time" % "2.12.7",
  "org.joda" % "joda-convert" % "2.2.3",
  "com.github.nscala-time" %% "nscala-time" % "2.18.0",
  "org.scalaz.stream" %% "scalaz-stream" % "0.8.6"
)

libraryDependencies ++= Seq( // test
  "junit" % "junit" % "4.12" % "test",
  "org.scalatest" %% "scalatest" % "3.2.18" % "test",
  "org.hamcrest" % "hamcrest-core" % "2.2" % "test",
  "org.specs2" %% "specs2-core" % "5.5.1" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test"
)

// org.scalastyle.sbt.ScalastylePlugin.Settings