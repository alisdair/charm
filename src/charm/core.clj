(ns charm.core
  (:require [instaparse.core :as insta])
  (:gen-class))

(def parser (insta/parser (clojure.java.io/resource "armv2.ebnf")))

(def arithmetic-opcode
  {
   "ADC" 2r0101
   "ADD" 2r0100
   "AND" 2r0000
   "BIC" 2r1110
   "EOR" 2r0001
   "ORR" 2r1100
   "RSB" 2r0011
   "RSC" 2r1111
   "SBC" 2r0110
   "SUB" 2r0010
   })

(def condition-opcode
  {
   "EQ" 2r0000
   "NE" 2r0001
   "CS" 2r0010
   "CC" 2r0011
   "MI" 2r0100
   "PL" 2r0101
   "VS" 2r0110
   "VC" 2r0111
   "HI" 2r1000
   "LS" 2r1001
   "GE" 2r1010
   "LT" 2r1011
   "GT" 2r1100
   "LE" 2r1101
   })

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
