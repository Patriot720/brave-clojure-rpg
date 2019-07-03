(ns brave-clojure-rpg.battle
  (:gen-class))
(declare critical-hit? armor-deflection get-weapon-dmg)

(defprotocol Warrior
  (calculate-damage [hero enemy weapon])
  (calculate-armor [person])
  (dead? [person])
  (damage [person damage]))

(defrecord Person [name hp weapons equipment]
  Warrior
  (calculate-damage [hero enemy weapon]
    (let [base-dmg (if (critical-hit?)
                     (* 2 (get-weapon-dmg hero weapon))
                     (get-weapon-dmg hero weapon))]
      (- base-dmg (* (armor-deflection enemy) base-dmg))))
  (calculate-armor [person]
    (reduce + (vals (:weapons person))))
  (dead? [person]
    (if (<= hp  0) true false))
  (damage [person damage]
    (assoc person :hp damage)))

(defn- armor-deflection [hero]
  (/ (calculate-armor hero) 100))

(defn- critical-hit? []
  (= (rand-int 5) 4))

(defn- get-weapon-dmg [hero weapon]
  (get-in hero [:weapons weapon]))
