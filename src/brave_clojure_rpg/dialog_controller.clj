(ns brave-clojure-rpg.dialog-controller
  (:require [clojure.data.json :as json])
  (:require [brave-clojure-rpg.battle :as person])
  (:gen-class))
(declare parse-dialog-json get-weapon-dmg-nth
         print-choices print-weapon-choices)

(defprotocol Dialog
  "Control dialog reactions"
  (display [x] "Print a dialog")
  (choose [dialog choice] "returns next dialog based on choice"))

(defrecord SimpleDialog [title description choices]
  Dialog
  (display [dialog]
    (println title)
    (println description)
    (print-choices choices "%d:%s"))
  (choose [dialog choice]  (get choices  choice)))
(defrecord BattleDialog [title description hero enemy
                         win-dialog]
  Dialog
  (display [dialog]
    (println "Battling with " (:name enemy))
    (println "Your hp " (:hp hero))
    (println "Enemy hp " (:hp enemy))
    (println "Attack with: \n")
    (print-weapon-choices (:weapons hero)))
  (choose [dialog choice]
    (let [damage (person/calculate-damage hero enemy
                                          (get-weapon-dmg-nth hero choice))
          damage-to-hero (person/calculate-damage enemy hero
                                                  (get-weapon-dmg-nth enemy 0))]
      (println (:name enemy) " attacked for " damage-to-hero " damage")
      (if (person/dead? enemy) win-dialog)
      (if (person/dead? hero) false)
      (->BattleDialog
       title description
       (person/damage hero damage-to-hero)
       (person/damage enemy damage)
       win-dialog))))

(defn- print-choices [choices fmt]
  (doseq [choice  choices i (range 0 (count choices))]
    (println (format fmt i (:title choice)))))

(defn- print-weapon-choices [choices]
  (doseq [weapon-choice choices
          i (range 1 (+ (count choices) 1))]
    (println i ":" (first weapon-choice) " " (last weapon-choice) " Damage")))
; Macro???

(defn parse-dialog-from-file [file]
  (parse-dialog-json (json/read-str file)))

(defn get-weapon-dmg-nth [person choice]
  (nth (keys (:weapons person)) choice 0))

(defn- parse-dialog-json ([json]
                          (->SimpleDialog (first json)
                                          (nth json 1)
                                          (map (fn [item] (parse-dialog-json item)) (last json)))))
