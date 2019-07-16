(ns brave-clojure-rpg.helpers
  (:require [brave-clojure-rpg.person :refer [->Person map->Person]]
            [brave-clojure-rpg.dialogs :as Dialogs]))

(def hero
  (->Person "Hero" 10 {:spear 3} {:headgear {:armor 3}}))

(def empty-hero
  (->Person "Hero" 10 {} {}))

(def enemy
  (->Person "Gremlin" 1 {:hands 1} {:one-true-ring {:armor 50}}))

(def simple-empty-dialog
  (Dialogs/->SimpleDialog "Fuck the police" "simple-empty-dialog" (map->Person {}) []))

(defn trim-carriage-return [string]
  (clojure.string/replace string "\r" ""))
