#!/bin/sh

sudo apt install clojure leiningen
set -a
. ./token.env
set +a
touch resources/data.yaml
lein deps
lein uberjar
java -jar ./target/uberjar/presenti-makerspace-0.1.0-SNAPSHOT-standalone.jar
