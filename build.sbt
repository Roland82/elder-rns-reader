name := """hello-scala"""

version := "1.0"

scalaVersion := "2.11.7"
com.github.retronym.SbtOneJar.oneJarSettings

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
libraryDependencies += "net.databinder.dispatch" %% "dispatch-core" % "0.13.2"
libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.18"
libraryDependencies += "joda-time" % "joda-time" % "2.9.9"
libraryDependencies += "jp.co.bizreach" %% "aws-ses-scala" % "0.0.2"
libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "10.0.11"

mainClass in oneJar := Some("uk.co.rnsreader.Main")

fork in run := true