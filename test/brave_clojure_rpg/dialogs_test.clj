(ns brave-clojure-rpg.dialogs-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialogs :as Dialogs]
            [brave-clojure-rpg.person :as bt]))
(def simple-empty-dialog  (Dialogs/->SimpleDialog "Fuck the police" "simple-empty-dialog" {} []))

(deftest dialog-parsing
  (let [parsed-dialog (Dialogs/parse-dialog-from-file (slurp "example_dialog.json"))
        expected-dialog (Dialogs/->SimpleDialog "Kill everyone" "You fucked up everyone" {}
                                                [(Dialogs/->SimpleDialog "Wtf" "other stuff happened" {} [])])
        nested-dialog (get-in expected-dialog [:choices 0])]

    (testing "Parsing example_dialog with one nested dialog"
      (is (=  parsed-dialog expected-dialog)))

    (testing "Choosing 0 should return nested dialog"
      (is (= (Dialogs/choose parsed-dialog 0)
             nested-dialog))))

  (testing "Battle dialog parsing"
    (let [parsed-dialog (Dialogs/parse-dialog-from-file (slurp "example_mixed_dialogs.json"))]
      (testing "Should return battle dialog with simple nested win dialog"
        (is (=  parsed-dialog
                (Dialogs/->BattleDialog "Battle with gremlin" ""
                                        (bt/->Person "Hero" 20 {:spear 10} nil)
                                        (bt/->Person "Gremlin" 20 {:hands 25} {})
                                        (Dialogs/->SimpleDialog "Winner" "Chicken dinner" (bt/->Person "Hero" 20 {:spear 10} nil) []))))))))

(deftest simple-dialog-test
  (let [dialog (Dialogs/->SimpleDialog "lulz" "wtf" {} [simple-empty-dialog])]

    (testing "Printing a dialog"
      (is (=
           (with-out-str (Dialogs/display dialog))
           "lulz\nwtf\n0:Fuck the police\n")))

    (testing "Choose dialog 0 should return first nested dialog"
      (is (= (Dialogs/choose dialog 0)  simple-empty-dialog)))

    (testing "Choosing dialog should return next dialog with HERO"
      (let [hero (bt/->Person "Hero" 20 {:spear 10} {})
            dialog (Dialogs/->SimpleDialog "lulz" "wtf" hero [simple-empty-dialog])]
        (is (= (:hero (Dialogs/choose dialog 0))
               hero))))))

; (deftest side-effect-dialog-test
;   (testing "Dialog choice that does damage to hero"
;     (let [dialog (Dialogs/->SideEffectDialog "lulz" "wtf"
;                                              (bt/->Person "Hero" 20 {} {}) [simple-empty-dialog])]
;       (is (= (:hp (:hero (Dialogs/choose dialog 0))) 15)))))

(deftest battle-dialog-test
  (let [hero (bt/->Person "Hero" 10 {:spear 25} {})
        gremlin (bt/->Person "Hero" 5 {:gg 2} {})
        win-dialog (Dialogs/->SimpleDialog "f" "f" {} [])
        dialog (Dialogs/->BattleDialog "the battle" "" hero gremlin win-dialog)]

    (testing "Choose shouldn't return nill"
      (is (not (nil? (Dialogs/choose dialog 0)))))

    (testing "Display dialog should return battle status"
      (is (= (with-out-str (Dialogs/display dialog))
             "Battling with  Hero\nYour hp  10\nEnemy hp  5\nAttack with: \n\n1 : :spear   25  Damage\n")))))
