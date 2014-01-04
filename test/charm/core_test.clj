(ns charm.core-test
  (:require [clojure.test :refer :all]
            [charm.core :refer :all]))

(deftest test-comment
  (is (= [:Source [:Comment "Test"]] (parser "; Test")))
  (is (= [:Source [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Register "R1"] [:Register "R2"] [:Immediate "5"]]] [:Comment "Test"]]] (parser "ADD R1, R2, #5 ; Test"))))

(deftest test-blank
  (is (= [:Source] (parser ""))))

(deftest test-blank-lines
  (is (= [:Source] (parser "\n\n")))
  (is (= [:Source [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Register "R1"] [:Register "R2"] [:Immediate "5"]]]]] (parser "\n\nADD R1, R2, #5\n\n"))))

(deftest test-label
  (is (= [:Source [:Label "print_char"]] (parser ".print_char")))
  (is (= [:Source [:Label "print_char" [:Comment "print a char"]]] (parser ".print_char ; print a char"))))

(deftest test-add-immediate
  (is (= [:Source [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Register "R1"] [:Register "R2"] [:Immediate "5"]]]]] (parser "ADD R1, R2, #5"))))

(deftest test-add-register
  (is (= [:Source [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Register "R1"] [:Register "R2"] [:Register "R3"]]]]] (parser "ADD R1, R2, R3"))))

(deftest test-add-register-shifted
  (is (= [:Source [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Register "R1"] [:Register "R2"] [:Register "R3"] [:Shift "RRX"]]]]] (parser "ADD R1, R2, R3, RRX")))
  (is (= [:Source [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Register "R1"] [:Register "R2"] [:Register "R3"] [:Shift "LSL" [:Immediate "15"]]]]]] (parser "ADD R1, R2, R3, LSL #15"))))

(deftest test-label-tied-to-following-instruction
  (is (= [:Source [:Label "print_char" [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Register "R1"] [:Register "R2"] [:Immediate "5"]]]]]] (parser ".print_char ADD R1, R2, #5")))
  (is (= [:Source [:Label "print_char" [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Register "R1"] [:Register "R2"] [:Immediate "5"]]]]]] (parser ".print_char\nADD R1, R2, #5"))))

(deftest test-condition
  (is (= [:Source [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Condition "EQ"] [:Status] [:Register "R1"] [:Register "R2"] [:Immediate "5"]]]]] (parser "ADDEQS R1, R2, #5")))
  (is (= [:Source [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Status] [:Register "R1"] [:Register "R2"] [:Immediate "5"]]]]] (parser "ADDS R1, R2, #5")))
  (is (= [:Source [:Statement [:Instruction [:Arithmetic [:ArithmeticOpcode "ADD"] [:Condition "EQ"] [:Register "R1"] [:Register "R2"] [:Immediate "5"]]]]] (parser "ADDEQ R1, R2, #5"))))
