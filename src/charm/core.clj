(ns charm.core
  (:require [instaparse.core :as insta])
  (:gen-class))

(def parser (insta/parser (clojure.java.io/resource "armv2.ebnf")))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
