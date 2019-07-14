(ns brave-clojure-rpg.person-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.person :as bt]))
(def hero
  (bt/->Person "Hero" 10 {:spear 3} {:headgear 3}))

(def enemy
  (bt/->Person "Gremlin" 1 {:hands 1} {:one-true-ring 50}))

(deftest attack-test
  (binding [bt/critical-hit-chance [0 25]] ; TODO rework chancing
    (is (= (:hp (bt/damage enemy 3))
           -1/2))))
(def critical-hit-chance 4)

(deftest attack-without-armor
  (binding [bt/critical-hit-chance [0 25]]
    (let [enemy (assoc enemy :equipment {})]
      (with-out-str (is (= (:hp (bt/damage enemy 3)) -2))))))

(deftest attack-critical-hit-test
  (binding [bt/critical-hit-chance [0 1]] ; TODO remove
    (is
     (some #{-2} (map  (fn [x] (:hp (bt/damage enemy 3)))
                       (range 1))))))
(deftest add-to-inventory-test
  (is (= (bt/add-to-inventory hero {:lul 25}) (assoc-in hero [:equipment :lul] 25))))
(deftest has?-test
  (is (= (bt/has? hero :spear) true))
  (is (= (bt/has? hero :lul) false)))
