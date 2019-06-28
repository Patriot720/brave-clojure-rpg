(ns brave-clojure-rpg.battle
  (:gen-class))

(defn print-battle-status [hero enemy]
  (println (str "Started battle with " (:name enemy)))
  (println (str "Your hp is:" (:hp hero)))
  (println (str "His hp is:" (:hp enemy)))
  (println "Your move"))

(defprotocol Warrior
  (calculate-damage [hero enemy weapon])
  (calculate-armour [person]))

(declare calculate-armour)
(defrecord Person [name hp weapons equipment]
  Warrior
  (calculate-damage [hero enemy weapon]
    (let [pure-dmg (get-in hero [:weapons weapon])
          armor-deflection (/ (calculate-armour enemy) 100)]
      (- pure-dmg (* armor-deflection pure-dmg))))
  (calculate-armour [person]
    (reduce + (vals (:weapons person)))))
