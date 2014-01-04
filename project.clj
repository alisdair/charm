(defproject charm "0.1.0-SNAPSHOT"
  :description "Some ARM stuff"
  :url "https://github.com/alisdair/charm"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [instaparse "1.2.14"]]
  :plugins [[quickie "0.2.4"]]
  :main ^:skip-aot charm.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
