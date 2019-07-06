(ns brave-clojure-rpg.person-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.person :as bt]))

(def hero
  (bt/->Person "Hero" 10 {:spear 3} {:headgear 3}))

(def enemy
  (bt/->Person "Gremlin" 1 {:hands 1} {:one-true-ring 50}))

(deftest attack-test
  (with-out-str (is (= (:hp (bt/damage enemy 3))
                       297/100))))

; Random generated result test
(deftest attack-critical-hit-test
  (is
   (with-out-str (some #{297/50} (map  (fn [x] (:hp (bt/damage enemy 3)))
                                       (range 50))))))
