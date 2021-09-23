#!/bin/sh

sudo apt install clojure leiningen
set -a
. ./token.env
set +a
touch resources/data.yaml
lein deps
lein uberjar
