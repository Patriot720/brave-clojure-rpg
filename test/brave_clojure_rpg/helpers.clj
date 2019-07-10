(ns brave-clojure-rpg.helpers)

(defn trim-carriage-return [string]
  (clojure.string/replace string "\r" ""))
