(ns brave-clojure-rpg.dialog-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialog-controller :as di]))

(let [other_dialog (di/->SimpleDialog "other-dialog" {})
      dialog (di/->SimpleDialog "lulz" {"Fuck the police" other_dialog})]
  (deftest print-a-dialog
    (is (=
         (with-out-str (di/print dialog))
         "lulz\r\n1:Fuck the police\r\n")))
  (deftest choosing-dialog-variant-should-return-new-dialog
    (is (= (di/choose dialog 1)  other_dialog))))
