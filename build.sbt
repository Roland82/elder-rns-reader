name := """stock-rns-reader"""

version := "1.0"
val http4sVersion = "0.17.6"
val scalazVersion = "7.2.18"
scalaVersion := "2.11.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"
libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
libraryDependencies += "org.scalaz" %% "scalaz-core" % scalazVersion
libraryDependencies += "org.scalaz" %% "scalaz-concurrent" % scalazVersion
libraryDependencies += "joda-time" % "joda-time" % "2.9.9"
libraryDependencies += "jp.co.bizreach" %% "aws-ses-scala" % "0.0.2"
libraryDependencies += "com.typesafe.akka" %% "akka-http-core" % "10.0.11"
libraryDependencies +="org.http4s" %% "http4s-dsl" % http4sVersion
libraryDependencies +="org.http4s" %% "http4s-blaze-server" % http4sVersion
libraryDependencies +="org.http4s" %% "http4s-blaze-client" % http4sVersion

fork in run := true