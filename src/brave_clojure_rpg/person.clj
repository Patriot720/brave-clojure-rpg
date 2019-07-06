(ns brave-clojure-rpg.person
  (:gen-class))
(declare calculate-damage
         critical-hit? armor-deflection get-weapon-dmg get-nth-weapon)

(defprotocol Warrior
  (dead? [person])
  (damage [person damage]))

(defrecord Person [name hp weapons equipment]
  Warrior
  (dead? [person]
    (if (<= hp  0) true false))
  (damage [person weapon-damage]
    (let [damage (calculate-damage person weapon-damage)]
      (println (:name person) " attacked for " damage " damage")
      (assoc person :hp damage))))

(defn- calculate-damage [hero weapon-damage]
  (let [base-dmg (if (critical-hit?)
                   (* 2 weapon-damage)
                   weapon-damage)]
    (- base-dmg (* (armor-deflection hero) base-dmg))))

(defn- calculate-armor [person]
  (reduce + (vals (:weapons person))))

(defn- armor-deflection [hero]
  (/ (calculate-armor hero) 100))
(defn- critical-hit? []
  (= (rand-int 5) 4))
