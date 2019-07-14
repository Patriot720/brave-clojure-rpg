(ns brave-clojure-rpg.parsing
  (:require [brave-clojure-rpg.dialogs :as Dialogs])
  (:require [clojure.data.json :as json])
  (:require [yaml.core :as yaml])
  (:require [brave-clojure-rpg.person :as person]))

 ; TODO different condition instead of counting array (dictionary)
 ; TODO add SideEffectDialog
(defn parse-dialog
  ([arr]
   (parse-dialog (person/map->Person (:hero arr {})) (get arr :dialogs)))
  ([hero arr]
   (case (clojure.string/lower-case (:type arr ""))
     "battle" (Dialogs/->BattleDialog (:title arr) (:description arr) hero (person/map->Person (:enemy arr)) (parse-dialog hero (:win-dialog arr)))
     "simple" (Dialogs/->SimpleDialog (:title arr)
                                      (:description arr)
                                      hero
                                      (into [] (map
                                                (fn [item] (parse-dialog hero item)) (:choices arr))))
     "sideeffect" (Dialogs/->SideEffectDialog (:title arr) (:description arr 1) hero (:side-effect arr 2) (parse-dialog hero (:choices arr)))
     "" [])))

(defmulti parse-dialog-from-file (fn [filename] (last (clojure.string/split filename #"\."))))
(defmethod parse-dialog-from-file "json"
  [filename]
  (parse-dialog (json/read-str (slurp filename) :key-fn keyword)))
(defmethod parse-dialog-from-file "yaml"
  [filename]
  (parse-dialog (yaml/parse-string (slurp filename) :keywords true)))

; (defn parse-dialog-from-file [file]
;   (let [json (json/read-str (slurp file) :key-fn keyword)
;         hero (person/map->Person (:hero json))]
;     (parse-dialog-json (if hero (person/map->Person (:hero json {})) {}) (get  json :dialogs))))
