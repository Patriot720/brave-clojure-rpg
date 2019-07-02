(ns brave-clojure-rpg.core
  (:require [brave-clojure-rpg.dialog-controller :as di])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
(def ^:dynamic input (fn [] 0))
(defn start-dialog [dialog]
  (di/display dialog)
  (let [next_dialog (di/choose dialog (Integer. (input)))]
    (println next_dialog)))
