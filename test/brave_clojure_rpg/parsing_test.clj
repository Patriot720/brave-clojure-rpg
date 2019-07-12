(ns brave-clojure-rpg.parsing-test
  (:require [brave-clojure-rpg.parsing :refer [parse-dialog-from-file]]
            [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialogs :as Dialogs]
            [brave-clojure-rpg.person :refer [->Person]]))

(deftest dialog-parsing
  (let [parsed-dialog (parse-dialog-from-file "example_dialog.json")
        expected-dialog (Dialogs/->SimpleDialog
                         "Kill everyone" "You fucked up everyone"
                         {}
                         [(Dialogs/->SimpleDialog "Wtf" "other stuff happened" {} [])])
        nested-dialog (get-in expected-dialog [:choices 0])]

    (testing "Parsing example_dialog with one nested dialog"
      (is (=  parsed-dialog expected-dialog)))

    (testing "Choosing 0 should return nested dialog"
      (is (= (Dialogs/choose parsed-dialog 0)
             nested-dialog))))

  (testing "Battle dialog parsing"
    (let [parsed-dialog
          (parse-dialog-from-file "example_mixed_dialogs.json")
          hero (->Person "Hero" 20 {:spear 10} nil)]
      (testing "Should return battle dialog with simple nested win dialog"
        (is (=  parsed-dialog
                (Dialogs/->BattleDialog "Battle with gremlin" ""
                                        hero
                                        (->Person "Gremlin" 20 {:hands 25} {})
                                        (Dialogs/->SimpleDialog
                                         "Winner" "Chicken dinner" hero
                                         [(Dialogs/->SideEffectDialog "Damaged" "For 5 dmg" hero 5 [])])))))))
  (testing "Breadth dialogs test"
    (let [parsed-dialog (parse-dialog-from-file "example_breadth_first_dialogs.json")]
      (is (= parsed-dialog (Dialogs/->SimpleDialog "" "" {} [(Dialogs/->SimpleDialog "" "" {} []) (Dialogs/->SimpleDialog "" "" {} [])]))))))

; TODO multiple same-level dialogs
