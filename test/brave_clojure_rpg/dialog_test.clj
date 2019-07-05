(ns brave-clojure-rpg.dialog-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialog :as Dialogs]
            [brave-clojure-rpg.battle :as bt]))
(def simple-empty-dialog  (Dialogs/->SimpleDialog "Fuck the police" "simple-empty-dialog" {} []))
; (deftest dialog-parsing
;   (let [file (slurp "example_dialog.json")]
;     (testing "Parsing example_dialog with one nested dialog"
;       (is (= (Dialogs/parse-dialog-from-file file)
;              (Dialogs/->SimpleDialog "Kill everyone" "You fucked up everyone"
;                                 [(Dialogs/->SimpleDialog "Wtf" "other stuff happened" [])]))))
;     (testing "Choosing 0 should return nested dialog"
;       (is (= (Dialogs/choose (Dialogs/parse-dialog-from-file file) 0)
;              (Dialogs/->SimpleDialog "Wtf" "other stuff happened" ())))))
;   (testing "Battle dialog parsing"
;     (let [file (slurp "example_mixed_dialogs.json")]
;       (testing "Should return battle dialog with simple nested win dialog"
;         (is (= (Dialogs/parse-dialog-from-file file)
;                (Dialogs/->BattleDialog "Battle" "With gremlin"
;                                   (bt/->Person "Hero" 20 {:spear 10} {})
;                                   (bt/->Person "Gremlin" 5 {:hands 1} {})
;                                   (Dialogs/->SimpleDialog "next" "dialog" []))))))))
; TODO test no weapons enemy
; TODO test no equipment hero,enemy
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

(deftest side-effect-dialog-test
  (testing "Dialog choice that does damage to hero"
    (let [dialog (Dialogs/->SideEffectDialog "lulz" "wtf"
                                             (bt/->Person "Hero" 20 {} {}) [simple-empty-dialog])]
      (is (= (:hp (:hero (Dialogs/choose dialog 0))) 15)))))
(deftest battle-dialog-test
  (let [hero (bt/->Person "Hero" 10 {:spear 25} {})
        gremlin (bt/->Person "Hero" 5 {:gg 2} {})
        win-dialog (Dialogs/->SimpleDialog "f" "f" {} [])
        dialog (Dialogs/->BattleDialog "the battle" "" hero gremlin win-dialog)]
    (testing "Get-nth-weapon should return weapon name"
      (is (= (Dialogs/get-nth-weapon hero 0) :spear))
      (is (= (Dialogs/get-nth-weapon hero 1) 0)))
    (testing "Choose shouldn't return nill"
      (is (not (nil? (Dialogs/choose dialog 0)))))
    (testing "Display dialog should return battle status"
      (is (= (with-out-str (Dialogs/display dialog))
             "Battling with  Hero\nYour hp  10\nEnemy hp  5\nAttack with: \n\n1 : :spear   25  Damage\n")))))
