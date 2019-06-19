(ns brave-clojure-rpg.battle
  (:gen-class))

(defn print-battle-status [hero enemy]
  (println (str "Started battle with " (:name enemy)))
  (println (str "Your hp is:" (:hp hero)))
  (println (str "His hp is:" (:hp enemy)))
  (println "Your move"))
