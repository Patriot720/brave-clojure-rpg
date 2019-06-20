(ns brave-clojure-rpg.battle-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.battle :as bt]))
(def hero {:hp 10 :weapons {:spear 3}
           :equipment {:headgear 3}})
(def enemy {:name "gremlin" :hp 1 :equipment {:one-true-ring 50}})
(deftest initalizing-battle-test
  (is (=
       (with-out-str (bt/print-battle-status hero enemy))
       "Started battle with gremlin\r\nYour hp is:10\r\nHis hp is:1\r\nYour move\r\n")))

(deftest attack-test
  (is (= (bt/attack hero enemy :spear)
         (assoc enemy :hp 0.9))))
