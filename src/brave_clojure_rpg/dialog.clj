(ns brave-clojure-rpg.dialog
  (:require [clojure.data.json :as json])
  (:require [brave-clojure-rpg.battle :as person])
  (:gen-class))
(declare parse-dialog-json get-nth-weapon
         print-choices print-weapon-choices)

(defprotocol Dialog
  "Control dialog reactions"
  (display [x] "Print a dialog")
  (choose [dialog choice] "returns next dialog based on choice"))

(defrecord SimpleDialog [title description hero choices]
  Dialog
  (display [dialog]
    (println title)
    (println description)
    (print-choices choices "%d:%s"))
  (choose [dialog choice]  (assoc (get choices  choice) :hero hero)))

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
                                          (get-nth-weapon hero choice))
          damage-to-hero (person/calculate-damage enemy hero
                                                  (get-nth-weapon enemy 0))]
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
  (parse-dialog-json (get (json/read-str file) "dialogs")))

(defn get-nth-weapon [person choice]
  (nth (keys (:weapons person)) choice 0))

(defn- parse-dialog-json ([json]
                          (if (= (count json) 4)
                            (->BattleDialog (first json) (nth json 1) {} (nth json 2) (parse-dialog-json (last json)))
                                ; TODO Same hero through the game
                                ; TODO Person instead of shit for nth json 2
                                ; TODO move to clj instead of json
                            (->SimpleDialog (first json)
                                            (nth json 1)
                                            (into [] (map
                                                      (fn [item] (parse-dialog-json item)) (last json)))))))
