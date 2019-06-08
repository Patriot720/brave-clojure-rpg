(ns brave-clojure-rpg.dialog-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialog-controller :as di]))

(deftest start-a-dialog
  (is (= (di/start (di/->SimpleDialog "lulz"))
         (di/->SimpleDialog )))
