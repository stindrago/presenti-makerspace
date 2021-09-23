(defproject presenti-makerspace "0.1.0-SNAPSHOT"
  :description "Telegram Bot to store presences for makerspace"

  :license {:name "The Unlincesed"
            :url "https://unlicense.org/"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ             "1.1.0"]
                 [morse               "0.2.4"]
                 [org.clojure/tools.namespace "0.2.7"]
                 [io.forward/yaml "1.0.11"]]

  :plugins [[lein-environ "1.1.0"]]

  :main ^:skip-aot presenti-makerspace.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
