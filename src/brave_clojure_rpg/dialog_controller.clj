(ns brave-clojure-rpg.dialog-controller
  (:require [clojure.data.json :as json])
  (:require [brave-clojure-rpg.battle :as bt])
  (:gen-class))
(declare parse-dialog-json)

(defprotocol Dialog
  "Control dialog reactions"
  (display [x] "Print a dialog")
  (choose [dialog choice] "returns next dialog based on choice"))

(defn start [dialog] (display dialog) (choose dialog (Integer. (read-line))))

(defrecord SimpleDialog [title description choices]
  Dialog
  (display [dialog]
    (println title)
    (doseq [choice  choices i (range 1 (+ (count choices) 1))]
      (println (str i ":" (:title choice)))))
  (choose [dialog choice]  (get choices  (- choice 1))))

(defrecord BattleDialog [title description hero enemy
                         win-dialog]
  Dialog
  (display [dialog])
  (choose [dialog choice]
    (let [damage (bt/calculate-damage hero enemy choice)
          hero-damage (bt/calculate-damage enemy hero 0)]
      (println (str (:name enemy) " attacked for " hero-damage " damage"))
      (if (<= (:hp enemy) 0)
        win-dialog)
      (if (<= (:hp hero) 0) false)
      (->BattleDialog
       title description
       (update hero :hp hero-damage)
       (update enemy :hp damage)
       win-dialog))))
(defn parse-dialog-from-file [file]
  (parse-dialog-json (json/read-str file)))

(defn- parse-dialog-json ([json]
                          (->SimpleDialog (first json)
                                          (nth json 1)
                                          (map (fn [item] (parse-dialog-json item)) (last json)))))
