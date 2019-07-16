(ns brave-clojure-rpg.person-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.person :as bt]))
(def hero
  (bt/->Person "Hero" 10 {:spear 3} {:headgear {:armor 3}}))

(def enemy
  (bt/->Person "Gremlin" 1 {:hands 1} {:one-true-ring {:armor 50}}))

(deftest attack-test
  (is (= (:hp (bt/damage enemy 3))
         -1/2)))

(deftest attack-without-armor
  (let [enemy (assoc enemy :equipment {})]
    (with-out-str (is (= (:hp (bt/damage enemy 3)) -2)))))

(deftest attack-critical-hit-test
  (is
   (some #{-2} (map  (fn [x] (:hp (bt/damage enemy 3)))
                     (range 1)))))

(deftest add-to-inventory-test
  (is (= (bt/add-to-inventory hero {:lul 25}) (assoc-in hero [:equipment :lul] 25))))
(deftest has?-test
  (is (= (bt/has? hero :spear) true))
  (is (= (bt/has? hero :lul) false)))
