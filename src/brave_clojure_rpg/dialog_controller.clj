(ns brave-clojure-rpg.dialog-controller
  (:require [clojure.data.json :as json])
  (:require [brave-clojure-rpg.battle :as bt])
  (:gen-class))
(declare parse-dialog-json get-weapon-dmg-nth)

(defprotocol Dialog
  "Control dialog reactions"
  (display [x] "Print a dialog")
  (choose [dialog choice] "returns next dialog based on choice"))

(defn- print-choices [choices]
  (doseq [choice  choices i (range 0 (count choices))]
    (println (str i ":" (:title choice)))))

(defrecord SimpleDialog [title description choices]
  Dialog
  (display [dialog]
    (println title)
    (print-choices choices))
  (choose [dialog choice]  (get choices  choice)))

(defrecord BattleDialog [title description hero enemy
                         win-dialog]
  Dialog
  (display [dialog]
    (println "Battling with " (:name enemy))
    (println "Your hp " (:hp hero))
    (println "Enemy hp " (:hp enemy))
    (println "Attack with: \n")
    (doseq [weapon-choice (:weapons hero)
            i (range 1 (+ (count (:weapons hero)) 1))]
      (println i ":" (first weapon-choice) " " (last weapon-choice) " Damage")))
  (choose [dialog choice]
    (let [damage (bt/calculate-damage hero enemy
                                      (get-weapon-dmg-nth hero choice))
          hero-damage (bt/calculate-damage enemy hero
                                           (get-weapon-dmg-nth enemy 0))]
      (println (str (:name enemy) " attacked for " hero-damage " damage"))
      (if (<= (:hp enemy) 0)
        win-dialog)
      (if (<= (:hp hero) 0) false)
      (->BattleDialog
       title description
       (assoc hero :hp hero-damage)
       (assoc enemy :hp damage)
       win-dialog))))

(defn parse-dialog-from-file [file]
  (parse-dialog-json (json/read-str file)))

(defn get-weapon-dmg-nth [person choice]
  (nth (keys (:weapons person)) choice 0))

(defn- parse-dialog-json ([json]
                          (->SimpleDialog (first json)
                                          (nth json 1)
                                          (map (fn [item] (parse-dialog-json item)) (last json)))))
