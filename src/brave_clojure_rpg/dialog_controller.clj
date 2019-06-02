(ns brave-clojure-rpg.dialog-controller
  (:gen-class))

(defprotocol Dialog
  "Control dialog reactions"
  (start [x] "Start a dialog"))

(defrecord SimpleDialog [title]
  Dialog
  (start [dialog] "Started a dialog"))
