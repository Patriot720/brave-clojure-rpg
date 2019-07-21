(ns brave-clojure-rpg.parsing-test
  (:require [brave-clojure-rpg.parsing :refer [parse-dialog-from-file]]
            [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialogs :as Dialogs]
            [brave-clojure-rpg.person :refer [->Person map->Person]]))

(deftest parsing-test
  (testing "SimpleDialogs"
    (let [expected-dialog (Dialogs/->SimpleDialog
                           "Kill everyone" "You fucked up everyone"
                           (map->Person {})
                           [(Dialogs/->SimpleDialog "Wtf" "other stuff happened" (map->Person {}) [])])]

      (testing "parse example_dialog with one nested dialog"
        (testing "JSON"
          (is (=  (parse-dialog-from-file "example_dialog.json") expected-dialog)))
        (testing "YAML"
          (is (=  (parse-dialog-from-file "example_dialog.yaml") expected-dialog))))))

  (testing "Battle/Simple dialogs "
    (let [hero (->Person "Hero" 20 {:spear {:damage 10}} nil 30) ; TODO to helpers
          expected (Dialogs/->BattleDialog "Battle with gremlin" ""
                                           hero
                                           (->Person "Gremlin" 20 {:hands {:damage 25 :critical-hit 15}} {} 35) ; TODO to helpers
                                           (Dialogs/->SimpleDialog
                                            "Winner" "Chicken dinner" hero
                                            [(Dialogs/->SideEffectDialog "Damaged" "For 5 dmg" hero 5 [])]))]
      (testing "Should return battle dialog with simple nested win dialog"
        (testing "JSON"
          (is (=  (parse-dialog-from-file "example_mixed_dialogs.json") expected)))
        (testing "YAML"
          (is (= (parse-dialog-from-file "example_mixed_dialogs.yaml") expected))))))

  (testing "Breadth-first dialogs test"
    (let [parsed-dialog (parse-dialog-from-file "example_breadth_first_dialogs.json")]
      (is (= parsed-dialog (Dialogs/->SimpleDialog "" "" (map->Person {}) [(Dialogs/->SimpleDialog "" "" (map->Person {}) []) (Dialogs/->SimpleDialog "" "" (map->Person {}) [])]))))))
