(ns brave-clojure-rpg.battle
  (:gen-class))

(defn print-battle-status [hero enemy]
  (println (str "Started battle with " (:name enemy)))
  (println (str "Your hp is:" (:hp hero)))
  (println (str "His hp is:" (:hp enemy)))
  (println "Your move"))

(defn attack [hero enemy weapon]
  (assoc enemy :hp
         (Double/parseDouble (format "%.1f" (-
                                             (get enemy :hp)
                                             (float (/ (get-in hero [:weapons weapon]) (reduce  #(+ %1 %2) (vals (get enemy :equipment))))))))))
