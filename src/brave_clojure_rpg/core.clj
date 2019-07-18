(ns brave-clojure-rpg.core
  (:require [brave-clojure-rpg.dialogs :as di]
            [brave-clojure-rpg.parsing :refer [parse-dialog-from-file]])
  (:gen-class))

(def ^:dynamic *input* (fn [] 25))

(defn start-dialog-loop [dialog]
  (di/display dialog)
  (let [next_dialog (di/choose dialog (Integer. (*input*)))]
    (when next_dialog
      (start-dialog-loop next_dialog))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (-> (first args)
      parse-dialog-from-file
      start-dialog-loop))
