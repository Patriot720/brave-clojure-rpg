(ns brave-clojure-rpg.test-helpers
  (:require [brave-clojure-rpg.person :refer [->Person map->Person]]
            [brave-clojure-rpg.dialogs :as Dialogs]))

(def hero
  (->Person "Hero" 10 {:spear 3} {:headgear {:armor 3}} 15))

(def empty-hero
  (->Person "Hero" 10 {} {} 15))

(def enemy
  (->Person "Gremlin" 5 {:hands 1} {:one-true-ring {:armor 50}} 10))

(def empty-enemy
  (->Person "Gremlin" 5 {} {} 10))

(def simple-empty-dialog
  (Dialogs/->SimpleDialog "Fuck the police" "simple-empty-dialog" (map->Person {}) []))

(defn trim-carriage-return [string]
  (clojure.string/replace string "\r" ""))
