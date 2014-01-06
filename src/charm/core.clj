(ns charm.core
  (:require [instaparse.core :as insta])
  (:gen-class))

(def parser (insta/parser (clojure.java.io/resource "armv2.ebnf")))

(defn unknown-instruction [instruction]
  (str "Unknown opcode: " (first instruction)))

(def condition-opcodes
  {
   "EQ" 2r0000 "NE" 2r0001 "CS" 2r0010 "CC" 2r0011 "MI" 2r0100
   "PL" 2r0101 "VS" 2r0110 "VC" 2r0111 "HI" 2r1000 "LS" 2r1001
   "GE" 2r1010 "LT" 2r1011 "GT" 2r1100 "LE" 2r1101 "AL" 2r1110
   })

(defn condition-bits [instruction]
  (let [condition (first (filter #(= (first %) :Condition) instruction))]
    (if condition
      (get condition-opcodes (second condition))
      (get condition-opcodes "AL"))))

(defn status-bit [instruction]
  (let [status (some #(= % [:Status]) instruction)]
    (if status
      1
      0)))

(def data-processing-opcodes
  {
   "AND" 2r0000 "EOR" 2r0001 "SUB" 2r0010 "RSB" 2r0011
   "ADD" 2r0100 "ADC" 2r0101 "SBC" 2r0110 "RSC" 2r0111
   "TST" 2r1000 "TEQ" 2r1001 "CMP" 2r1010 "CMN" 2r1011
   "ORR" 2r1100 "MOV" 2r1101 "BIC" 2r1110 "MVN" 2r1111
   })

(defn opcode-bits [instruction opcodes]
  (let [opcode (first instruction)]
    (get opcodes opcode)))

(def registers
  {
   ; Numbered
   "R0" 0 "R1" 1 "R2" 2 "R3" 3 "R4" 4 "R5" 5 "R6" 6 "R7" 7 "R8" 8
   "R9" 9 "R10" 10 "R11" 11 "R12" 12 "R13" 13 "R14" 14 "R15" 15
   ; APCS
   "A1" 0 "A2" 1 "A3" 2 "A4" 3
   "V1" 4 "V2" 5 "V3" 6 "V4" 7 "V5" 8 "V6" 9
   "SL" 10 "FP" 11 "IP" 12 "SP" 13 "LR" 14 "PC" 15
   })

(defn dest-register [instruction]
  (let [register (first (filter #(= (first %) :Register) instruction))]
    (get registers (second register))))

(defn op-1-register [instruction]
  (let [register (second (filter #(= (first %) :Register) instruction))]
    (get registers (second register))))

(defn immediate-bit [instruction]
  (if (= (first (last instruction)) :Immediate)
    1
    0))

(defn immediate-encode [number]
  ; FIXME
  0
  )

(defn shifted-register-encode [operand]
  ; FIXME
  0
  )

(defn op-2-bits [instruction]
  (let [op-2 (drop 4 instruction)]
    (if (= (first (first op-2)) :Immediate)
      (immediate-encode (rest (first op-2)))
      (shifted-register-encode op-2))))

(defn three-operand [instruction]
  (let [condition (condition-bits instruction)
        immediate (immediate-bit  instruction)
        opcode    (opcode-bits    instruction data-processing-opcodes)
        status    (status-bit     instruction)
        op-1      (op-1-register  instruction)
        dest      (dest-register  instruction)
        op-2      (op-2-bits      instruction)]
    (bit-or
      (bit-shift-left condition 28)
      (bit-shift-left immediate 25)
      (bit-shift-left opcode    21)
      (bit-shift-left status    20)
      (bit-shift-left op-1      16)
      (bit-shift-left dest      12)
      (bit-shift-left op-2       0))))

(defn assemble-instruction [instruction]
  (let [jump-table {
         :Arithmetic          three-operand
         :Move                three-operand
         :Compare             three-operand
         ; :Branch              branch
         ; :SoftwareInterrupt   software-interrupt
         ; :Multiply            multiply
         ; :MultiplyAccumulate  multiply
         ; :Load                load-store
         ; :Store               load-store
         ; :Swap                load-store
         ; :LoadMultiple        load-store-multiple
         ; :StoreMultiple       load-store-multiple
         }
        instruction-type (first instruction)
        assembler        (or
                           (get jump-table instruction-type)
                           unknown-instruction)]
    (assembler (rest instruction))))

(defn assemble [lines]
  (map #(assemble-instruction (second %))
       (filter #(= (first %) :Instruction) lines)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
