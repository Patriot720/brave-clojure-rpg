(ns brave-clojure-rpg.parsing
  (:require [brave-clojure-rpg.dialogs :as Dialogs])
  (:require [clojure.data.json :as json])
  (:require [brave-clojure-rpg.person :as person]))

 ; TODO different condition instead of counting array (dictionary)
 ; TODO add SideEffectDialog
(defn- parse-dialog-json ([hero json]
                          (let [real-json (drop 1 json)]
                            (case (clojure.string/lower-case (nth json 0 ""))
                              "battle" (Dialogs/->BattleDialog (first real-json) (nth real-json 1) hero (person/map->Person (nth real-json 2)) (parse-dialog-json hero (last real-json)))
                              "simple" (Dialogs/->SimpleDialog (first real-json)
                                                               (nth real-json 1)
                                                               hero
                                                               (into [] (map
                                                                         (fn [item] (parse-dialog-json hero item)) (last real-json))))
                              "sideeffect" (Dialogs/->SideEffectDialog (first real-json) (nth real-json 1) hero (nth real-json 2) (parse-dialog-json hero (last real-json)))
                              "" []))))

(defn parse-dialog-from-file [file]
  (let [json (json/read-str (slurp file) :key-fn keyword)
        hero (:hero json)]
    (parse-dialog-json (if hero (person/map->Person (:hero json {})) {}) (get  json :dialogs))))
