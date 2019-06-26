(ns brave-clojure-rpg.battle-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.battle :as bt]))
(def hero
  (bt/->Person "Hero" 10 {:spear 3} {:headgear 3}))

(def enemy
  (bt/->Person "Gremlin" 1 {:hands 1} {:one-true-ring 50}))

(deftest initalizing-battle-test
  (is (=
       (with-out-str (bt/print-battle-status hero enemy))
       "Started battle with Gremlin\nYour hp is:10\nHis hp is:1\nYour move\n")))

(deftest attack-test
  (is (= (bt/attack hero enemy :spear)
         (assoc enemy :hp 0.9))))
