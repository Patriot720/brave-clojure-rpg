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
  (let [enemy (assoc-in enemy [:equipment :one-true-ring :critical-hit] 99)]
    (is
     (some #{-2} (map  (fn [x] (:hp (bt/damage enemy 3)))
                       (range 1))))))

(deftest equip-weapon-test
  (let [hero (bt/->Person "Hero" 10 {} {:lulz {:type "weapon" :damage 3} :wtf {:armor 5}})
        expected-hero (bt/->Person "Hero" 10 {:lulz {:type "weapon" :damage 3}} {:wtf {:armor 5}})]
    (is (= (bt/equip hero :lulz) expected-hero))
    (is (thrown? Exception (bt/equip hero :wtf)))))
(deftest add-to-inventory-test
  (is (= (bt/add-to-inventory hero {:lul 25}) (assoc-in hero [:equipment :lul] 25))))
(deftest has?-test
  (is (= (bt/has? hero :spear) true))
  (is (= (bt/has? hero :lul) false)))
