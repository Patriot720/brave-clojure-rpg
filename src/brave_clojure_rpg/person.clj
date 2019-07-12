(ns brave-clojure-rpg.person
  (:gen-class))

(def critical-hit-number 4) ; TODO unclear terminology

(defn- calculate-armor [person]
  (reduce + (vals (:equipment person))))

(defn- armor-deflection [hero]
  (/ (calculate-armor hero) 100))

(defn- critical-hit? []
  (= (rand-int 5) critical-hit-number))

(defn- calculate-damage-to [hero weapon-damage]
  (let [base-dmg (if (critical-hit?)
                   (* 2 weapon-damage)
                   weapon-damage)]
    (- base-dmg (* (armor-deflection hero) base-dmg))))

(defprotocol Warrior
  (dead? [person])
  (damage [person damage]))

(defrecord Person [name hp weapons equipment]
  Warrior
  (dead? [person]
    (if (<= hp  0) true false))
  (damage [person weapon-damage]
    (let [damage (calculate-damage-to person weapon-damage)]
      (println (:name person) " attacked for " damage " damage")
      (assoc person :hp (- hp damage)))))
