(ns brave-clojure-rpg.dialog-controller
  (:gen-class))

(defprotocol Dialog
  "Control dialog reactions"
  (start [x] "Start a dialog"))

(defrecord SimpleDialog [dialog]
  Dialog
  (start [dialog] (->SimpleDialog (assoc-in dialog [:finished] 1))))
