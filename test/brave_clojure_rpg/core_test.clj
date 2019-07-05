(ns brave-clojure-rpg.core-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialog :as di]
            [brave-clojure-rpg.core :as d]))

(defn my-read-line [] 0)
(deftest dialog-loop-test
  (binding [d/input (fn [] 0)]
    (is (=
         (with-out-str
           (d/start-dialog (di/parse-dialog-from-file (slurp "test_dialog.json"))))
         "Kill everyone\nYou fucked up everyone\n0:Wtf\nWtf\nother stuff happened\n"))))
