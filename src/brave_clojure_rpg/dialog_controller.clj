(ns brave-clojure-rpg.dialog-controller
  (:require [clojure.data.json :as json])
  (:gen-class))
(declare parse-dialog-json)
(defprotocol Dialog
  "Control dialog reactions"
  (start [dialog])
  (print [x] "Print a dialog")
  (choose [dialog choice] "returns next dialog based on choice"))

(defrecord SimpleDialog [title description choices]
  Dialog
  (start [dialog] (print dialog) (choose dialog (Integer. (read-line))))
  (print [dialog]
    (println title)
    (doseq [choice  choices i (range 1 (+ (count choices) 1))]
      (println (str i ":" (:title choice)))))
  (choose [dialog choice]  (get choices  (- choice 1))))

(defn parse-dialog-from-file [file]
  (parse-dialog-json (json/read-str file)))

(defn- parse-dialog-json ([json]
                          (->SimpleDialog (first json)
                                          (nth json 1)
                                          (map (fn [item] (parse-dialog-json item)) (last json)))))
