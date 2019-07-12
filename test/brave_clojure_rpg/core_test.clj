(ns brave-clojure-rpg.core-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.parsing :refer [parse-dialog-from-file]]
            [brave-clojure-rpg.core :as d]
            [brave-clojure-rpg.helpers :as helpers]))

(defn my-read-line [] 0)
(deftest dialog-loop-test
  (testing "Should return dialogs print strings"
    (binding [d/input (fn [] 0)]
      (is (=
           (helpers/trim-carriage-return (with-out-str
                                           (d/start-dialog-loop (parse-dialog-from-file "example_dialog.json"))))
           "Kill everyone\nYou fucked up everyone\n0:Wtf\nWtf\nother stuff happened\n")))))
