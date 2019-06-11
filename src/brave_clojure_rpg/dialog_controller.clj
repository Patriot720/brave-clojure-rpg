(ns brave-clojure-rpg.dialog-controller
  (:gen-class))

(defprotocol Dialog
  "Control dialog reactions"
  (print [x] "Print a dialog")
  (choose [dialog choice] "returns next dialog based on choice"))

(defrecord SimpleDialog [title choices]
  Dialog
  (print [dialog]
    (println title)
    (doseq [choice (keys choices) i (range 1 (+ (count choices) 1))]
      (println (str i ":" choice))))
  (choose [dialog choice] (get choices (nth (keys choices) (- choice 1)))))
