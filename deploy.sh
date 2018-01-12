#!/usr/bin/env bash

/usr/local/bin/sbt one-jar

cp target/scala-2.11/hello-scala_2.11-1.0.jar reader.jar