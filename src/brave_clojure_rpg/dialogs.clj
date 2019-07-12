(ns brave-clojure-rpg.dialogs
  (:require [brave-clojure-rpg.person :as person])
  (:gen-class))

(defn- apply-to-choice [choice args]
  (map (fn [getter] (getter choice)) args))

(defn- print-choices [choices fmt & args]
  (doseq [choice  choices i (range 0 (count choices))]
    (println (apply format fmt i (apply-to-choice choice args)))))

(defn- print-simple-choices [choices]
  (print-choices choices "%d:%s" :title))

(defn- print-weapon-choices [choices]
  (print-choices choices "%d : %s %s Damage" first last))

(defn- get-weapon-dmg [person choice]
  (get-in person [:weapons (nth (keys (:weapons person)) choice 0)]))

(defn- pass-hero-to-next-dialog [dialog hero]
  (assoc  dialog :hero hero))

(defprotocol Dialog
  "Control dialog reactions"
  (display [x] "Print a dialog")
  (choose [dialog choice] "returns next dialog based on choice"))

(defrecord SimpleDialog [title description hero choices]
  Dialog
  (display [dialog]
    (println title)
    (println description)
    (print-simple-choices choices))
  (choose [dialog choice]
    (if-let [next_dialog (get choices  choice)]
      (pass-hero-to-next-dialog next_dialog hero))))

(defrecord SideEffectDialog [title description hero side-damage choices]
  Dialog
  (display [dialog] ; TODO duplaction from simpledialog
; TODO go through battle dialog
    (println title)
    (println description)
    (print-simple-choices choices))
  (choose [dialog choice]
    (let [damaged-hero (person/damage hero side-damage)]
      (if (person/dead? damaged-hero) false
          (if-let [next_dialog (get choices  choice)] ; TODO duplcation from battledialog
            (pass-hero-to-next-dialog next_dialog damaged-hero))))))

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
    (let [damaged-hero (person/damage hero (get-weapon-dmg enemy 0))
          damaged-enemy (person/damage enemy (get-weapon-dmg hero choice))]
      (cond
        (person/dead? damaged-enemy) (pass-hero-to-next-dialog win-dialog hero)
        (person/dead? damaged-hero) false
        :else (->BattleDialog
               title description
               damaged-hero
               damaged-enemy
               win-dialog)))))
