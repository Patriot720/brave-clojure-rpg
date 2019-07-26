(ns brave-clojure-rpg.person
  (:gen-class))

(defn- reduce-map-or-maps [ifone ifmore map]
  (if (> (count map) 1)
    (reduce  ifmore map)
    (ifone map)))

(defn- calculate-armor [person]
  (reduce-map-or-maps #(:armor (first %1) 0)
                      #(+ (:armor %1) (:armor %2))
                      (vals (:inventory person))))

(defn- armor-deflection [hero]
  (/ (calculate-armor hero) 100))

(defn- get-critical-hit-chances [hero]
  (+ (reduce-map-or-maps #(:critical-hit (first %1) 0)
                         #(+ (:critical-hit %1) (:critical-hit %2))
                         (vals (:inventory hero))) ; TODO equipped equipment
     (reduce-map-or-maps #(:critical-hit (first %1) 0)
                         #(+ (:critical-hit %1) (:critical-hit %2))
                         (vals (:equipment hero)))))

(defn- critical-hit? [hero]
  (= (inc (rand-int (- 100 (get-critical-hit-chances hero)))) 1))

(defn- calculate-damage-to [hero weapon-damage]
  (let [base-dmg (if (critical-hit? hero)
                   (* 2 weapon-damage)
                   weapon-damage)]
    (- base-dmg (* (armor-deflection hero) base-dmg))))

(defprotocol Warrior
  (dead? [person])
  (damage [person damage])
  (add-to-inventory [person item])
  (has? [person item])
  (equip [person weapon])
  (equipped? [person thing]))

(defrecord Person [name hp equipment inventory max-hp]
  Warrior
  (dead? [person]
    (if (<= hp  0) true false))
  (damage [person weapon-damage]
    (let [damage (calculate-damage-to person weapon-damage)]
      (println (:name person) " attacked for " damage " damage")
      (assoc person :hp (- hp damage))))
  (equipped? [person thing]
    (or (contains? (:headgear equipment) thing)
        (contains? (:weapon equipment) thing)
        (contains? (:ring equipment) thing)
        (contains? (:armor equipment) thing)))
  (has? [person item]
    (if (or (equipped? person item) (contains? inventory item))
      true
      false))
  (equip [person weapon]
    (if (= (:type (weapon inventory)) "weapon")
      (let [person (assoc-in person [:equipment weapon] (weapon inventory))]
        (assoc person :inventory (dissoc inventory weapon)))
      (throw (Exception. "Not a weapon"))))
  (add-to-inventory [person item]
    (update person :inventory conj item)))
