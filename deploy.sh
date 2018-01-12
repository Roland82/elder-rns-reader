#!/usr/bin/env bash

/usr/local/bin/sbt assembly

cp target/scala-2.11/stock-rns-reader-assembly-1.0.jar stock-rns-reader.jar