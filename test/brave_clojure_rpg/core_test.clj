(ns brave-clojure-rpg.core-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialogs :as di]
            [brave-clojure-rpg.core :as d]))

(defn my-read-line [] 0)
(deftest dialog-loop-test
  (testing "Should return dialogs print strings"
    (binding [d/input (fn [] 0)]
      (is (=
           (with-out-str
             (d/start-dialog-loop (di/parse-dialog-from-file (slurp "example_dialog.json"))))
           "Kill everyone\nYou fucked up everyone\n0:Wtf\nWtf\nother stuff happened\n")))))
