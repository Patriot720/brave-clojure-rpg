(ns brave-clojure-rpg.dialogs
  (:require [brave-clojure-rpg.person :as person]
            [brave-clojure-rpg.helpers :refer [condt]])
  (:gen-class))

(defn- pass-hero-to-next-dialog [dialog hero]
  (assoc  dialog :hero hero))

(defn- simple-print [dialog]
  (println (:title dialog))
  (println (:description dialog))
  (let [choices (:choices dialog)]
    (doseq [choice choices i (range (count choices))]
      (printf "%d:%s\n" i (:title choice)))))

(defprotocol Dialog
  "Control dialog reactions"
  (display [x] "Print a dialog")
  (choose [dialog choice] "returns next dialog based on choice"))

(defrecord EmptyDialog []
  Dialog
  (choose [dialog choice])
  (display [dialog]))

(defrecord ConditionDialog [title success fail hero has-item reward previous-dialog]
  Dialog
  (display [dialog]
    (if (person/has? hero has-item)
      (println success)
      (println fail)))
  (choose [dialog choice]
    (if (person/has? hero has-item) ; TODO placeholder
      (pass-hero-to-next-dialog previous-dialog
                                (person/add-to-inventory hero reward))
      previous-dialog)))

(defrecord SimpleDialog [title description hero choices]
  Dialog
  (display [dialog]
    (simple-print dialog))
  (choose [dialog choice]
    (if-let [next_dialog (get choices  choice)]
      (pass-hero-to-next-dialog next_dialog hero)
      (->EmptyDialog))))

(defn- apply-effects [hero side-effects]
  (loop [side-keys (keys side-effects) hero hero]
    (case (first side-keys)
      :damage (recur (next side-keys) (person/damage hero (:damage side-effects)))
      :weapon (recur (next side-keys) (person/add-to-inventory hero (:weapon side-effects)))
      hero)))

(defrecord SideEffectDialog [title description hero side-effects choices]
  Dialog
  (display [dialog]
    (simple-print dialog))
  (choose [dialog choice]
    (let [updated-hero (apply-effects hero side-effects)]
      (if (person/dead? updated-hero) (->EmptyDialog)
          (when-let [next_dialog (get choices  choice)]
            (pass-hero-to-next-dialog next_dialog updated-hero))))))

(defrecord BattleDialog [title description hero enemy
                         win-dialog]
  Dialog

  (display [dialog]
    (println "Battling with " (:name enemy))
    (println "Your hp " (:hp hero))
    (println "Enemy hp " (:hp enemy))
    (println "Attack with: \n")
    (let [weapon (:weapon (:equipment hero))
          weapon-name (first (keys weapon))] ; TODO array instead of assoc for weapon
      (println "0:" weapon-name (:damage (weapon-name weapon)) "damage")))

  (choose [dialog choice]
    (let [damaged-hero (person/attack hero enemy)
          damaged-enemy (person/attack enemy hero)]
      (condt person/dead?
             damaged-enemy (pass-hero-to-next-dialog win-dialog damaged-hero)
             damaged-hero (->EmptyDialog)
             (->BattleDialog
              title description
              damaged-hero
              damaged-enemy
              win-dialog)))))
