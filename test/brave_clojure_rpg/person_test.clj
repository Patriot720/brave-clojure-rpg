(ns brave-clojure-rpg.person-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.person :as bt]))
(def critical-hit-number 25)
(def hero
  (bt/->Person "Hero" 10 {:spear 3} {:headgear 3}))

(def enemy
  (bt/->Person "Gremlin" 1 {:hands 1} {:one-true-ring 50}))

(deftest attack-test
  (is (= (:hp (bt/damage enemy 3))
         -1/2)))
(def critical-hit-number 4)

(deftest attack-without-armor
  (let [hero (assoc hero :equipment {})
        enemy (assoc enemy :equipment {})]
    (with-out-str (is (= (:hp (bt/damage enemy 3)) -2)))))
; Random generated result test
(deftest attack-critical-hit-test
  (is
   (some #{-2} (map  (fn [x] (:hp (bt/damage enemy 3)))
                     (range 50)))))
