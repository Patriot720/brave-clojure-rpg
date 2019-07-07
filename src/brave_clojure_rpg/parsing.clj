(ns brave-clojure-rpg.parsing
  (:require [brave-clojure-rpg.dialogs :as Dialogs])
  (:require [clojure.data.json :as json])
  (:require [brave-clojure-rpg.person :as person]))

(defn- parse-dialog-json ([hero json]
                          (if (= (count json) 4)
                            (Dialogs/->BattleDialog (first json) (nth json 1) hero (person/map->Person (nth json 2)) (parse-dialog-json hero (last json)))
                                ; TODO Same hero through the game
                            (Dialogs/->SimpleDialog (first json)
                                                    (nth json 1)
                                                    hero
                                                    (into [] (map
                                                              (fn [item] (parse-dialog-json hero item)) (last json)))))))

(defn parse-dialog-from-file [file]
  (let [json (json/read-str file :key-fn keyword)
        hero (:hero json)]
    (parse-dialog-json (if hero (person/map->Person (:hero json {})) {}) (get  json :dialogs))))
