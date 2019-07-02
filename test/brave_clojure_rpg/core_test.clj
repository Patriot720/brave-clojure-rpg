(ns brave-clojure-rpg.core-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialog-controller :as di]
            [brave-clojure-rpg.core :as d]))

(defn my-read-line [] 1)
(binding [d/input my-read-line]
  (deftest dialog-loop-test
    (d/start-dialog (di/->SimpleDialog "Lul" "vul" {"wtf" (di/->SimpleDialog "" "" {})}))))
;   (deftest dialog-loop-test
;     (is (d/start-dialog (di/->SimpleDialog "Lul" "vul" {"wtf" (di/->SimpleDialog "" "" {})})))))
