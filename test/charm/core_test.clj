(ns charm.core-test
  (:require [clojure.test :refer :all]
            [charm.core :refer :all]))

(deftest test-comment
  (is (= [[:Comment "Test"]] (parser "; Test")))
  (is (= [[:Instruction [:Arithmetic "ADD" [:Register "R1"] [:Register "R2"] [:Immediate [:DecimalNumber "5"]]]] [:Comment "Test"]] (parser "ADD R1, R2, #5 ; Test"))))

(deftest test-blank
  (is (= [] (parser ""))))

(deftest test-blank-lines
  (is (= [] (parser "\n\n")))
  (is (= [[:Instruction [:Arithmetic "ADD" [:Register "R1"] [:Register "R2"] [:Immediate [:DecimalNumber "5"]]]]] (parser "\n\nADD R1, R2, #5\n\n"))))

(deftest test-label
  (is (= [[:Label [:Identifier "print_char"]]] (parser ".print_char")))
  (is (= [[:Label [:Identifier "print_char"] [:Comment "print a char"]]] (parser ".print_char ; print a char"))))

(deftest test-add-immediate
  (is (= [[:Instruction [:Arithmetic "ADD" [:Register "R1"] [:Register "R2"] [:Immediate [:DecimalNumber "5"]]]]] (parser "ADD R1, R2, #5"))))

(deftest test-add-register
  (is (= [[:Instruction [:Arithmetic "ADD" [:Register "R1"] [:Register "R2"] [:Register "R3"]]]] (parser "ADD R1, R2, R3"))))

(deftest test-add-register-shifted
  (is (= [[:Instruction [:Arithmetic "ADD" [:Register "R1"] [:Register "R2"] [:Register "R3"] [:Shift "RRX"]]]] (parser "ADD R1, R2, R3, RRX")))
  (is (= [[:Instruction [:Arithmetic "ADD" [:Register "R1"] [:Register "R2"] [:Register "R3"] [:Shift "LSL" [:Register "R4"]]]]] (parser "ADD R1, R2, R3, LSL R4")))
  (is (= [[:Instruction [:Arithmetic "ADD" [:Register "R1"] [:Register "R2"] [:Register "R3"] [:Shift "LSL" [:Immediate [:DecimalNumber "15"]]]]]] (parser "ADD R1, R2, R3, LSL #15"))))

(deftest test-label-tied-to-following-instruction
  (is (= [[:Label [:Identifier "print_char"] [:Instruction [:Arithmetic "ADD" [:Register "R1"] [:Register "R2"] [:Immediate [:DecimalNumber "5"]]]]]] (parser ".print_char ADD R1, R2, #5")))
  (is (= [[:Label [:Identifier "print_char"] [:Instruction [:Arithmetic "ADD" [:Register "R1"] [:Register "R2"] [:Immediate [:DecimalNumber "5"]]]]]] (parser ".print_char\nADD R1, R2, #5"))))

(deftest test-condition
  (is (= [[:Instruction [:Arithmetic "ADD" [:Condition "EQ"] [:Status] [:Register "R1"] [:Register "R2"] [:Immediate [:DecimalNumber "5"]]]]] (parser "ADDEQS R1, R2, #5")))
  (is (= [[:Instruction [:Arithmetic "ADD" [:Status] [:Register "R1"] [:Register "R2"] [:Immediate [:DecimalNumber "5"]]]]] (parser "ADDS R1, R2, #5")))
  (is (= [[:Instruction [:Arithmetic "ADD" [:Condition "EQ"] [:Register "R1"] [:Register "R2"] [:Immediate [:DecimalNumber "5"]]]]] (parser "ADDEQ R1, R2, #5"))))

(deftest test-move
  (is (= [[:Instruction [:Move "MOV" [:Status] [:Register "V1"] [:Register "V2"] [:Shift "LSR" [:Immediate [:DecimalNumber "12"]]]]]] (parser "MOVS V1, V2, LSR #12"))))

(deftest test-branch
  (is (= [[:Instruction [:Branch "B" [:Address [:HexNumber "00FF"]]]]] (parser "B &00FF")))
  (is (= [[:Instruction [:Branch "B" [:Address [:DecimalNumber "32"]]]]] (parser "B 32")))
  (is (= [[:Instruction [:Branch "BL" [:Condition "EQ"] [:Address [:Identifier "loop"]]]]] (parser "BLEQ loop"))))

(deftest test-multiply
  (is (= [[:Instruction [:Multiply "MUL" [:Register "R1"] [:Register "R2"] [:Register "R3"]]]] (parser "MUL R1, R2, R3"))))

(deftest test-multiply-accumulate
  (is (= [[:Instruction [:MultiplyAccumulate "MLA" [:Condition "GT"] [:Register "R1"] [:Register "R2"] [:Register "R3"] [:Register "R4"]]]] (parser "MLAGT R1, R2, R3, R4"))))

(deftest test-load
  (is (= [[:Instruction [:Load "LDR" [:Byte] [:Register "R0"] [:LoadStoreAddress [:Register "R1"] [:Offset [:Immediate [:DecimalNumber "4"]]]]]]] (parser "LDRB R0, [R1, #4]")))
  (is (= [[:Instruction [:Load "LDR" [:Register "R0"] [:LoadStoreAddress [:Register "R1"] [:PostIndexOffset [:Immediate [:DecimalNumber "4"]]]]]]] (parser "LDR R0, [R1], #4")))
  (is (= [[:Instruction [:Load "LDR" [:Register "R0"] [:LoadStoreAddress [:Register "R1"] [:PreIndexOffset [:SignedImmediate [:Sign "-"] [:DecimalNumber "4"]]]]]]] (parser "LDR R0, [R1, #-4]!")))
  (is (= [[:Instruction [:Load "LDR" [:Register "R0"] [:LoadStoreAddress [:Register "R1"] [:Offset [:Sign "-"] [:Register "R2"]]]]]] (parser "LDR R0, [R1, -R2]")))
  (is (= [[:Instruction [:Load "LDR" [:Condition "EQ"] [:Translate] [:Register "R0"] [:LoadStoreAddress [:Register "R1"] [:PreIndexOffset [:Immediate [:DecimalNumber "4"]]]]]]] (parser "LDREQT R0, [R1, #4]!"))))

(deftest test-store
  (is (= [[:Instruction [:Store "STR" [:Byte] [:Register "R0"] [:LoadStoreAddress [:Register "R1"] [:Offset [:Immediate [:DecimalNumber "4"]]]]]]] (parser "STRB R0, [R1, #4]"))))

(deftest test-swap
  (is (= [[:Instruction [:Swap "SWP" [:Register "R0"] [:Register "R0"] [:Register "R1"]]]] (parser "SWP R0, R0, [R1]"))))

(deftest test-load-multiple
  (is (= [[:Instruction [:LoadMultiple "LDM" [:MultipleType "FD"] [:Register "R13"] [:RegisterList [:RegisterRange [:Register "R10"] [:Register "R13"]]] [:Banked]]]] (parser "LDMFD R13, {R10-R13}^")))
  (is (= [[:Instruction [:LoadMultiple "LDM" [:MultipleType "IA"] [:Register "R13"] [:WriteBack] [:RegisterList [:RegisterRange [:Register "R0"] [:Register "R3"]] [:Register "PC"]]]]] (parser "LDMIA R13!, {R0-R3, PC}"))))
