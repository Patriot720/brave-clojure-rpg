(ns brave-clojure-rpg.dialog-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialog-controller :as di]))

(deftest start-a-dialog
  (is (=
       (with-out-str (di/print (di/->SimpleDialog "lulz" {"Fuck the police" "lulz"})))
       "lulz\r\n1:Fuck the police\r\n")))
