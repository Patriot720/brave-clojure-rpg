(ns brave-clojure-rpg.dialog-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialog-controller :as di]
            [brave-clojure-rpg.battle :as bt]))

(deftest dialog-parsing
  (let [file (slurp "example_dialog.json")]
    (testing "Parsing example_dialog with one nested dialog"
      (is (= (di/parse-dialog-from-file file)
             (di/->SimpleDialog "Kill everyone" "You fucked up everyone"
                                [(di/->SimpleDialog "Wtf" "other stuff happened" [])]))))
    (testing "Choosing 0 should return nested dialog"
      (is (= (di/choose (di/parse-dialog-from-file file) 0)
             (di/->SimpleDialog "Wtf" "other stuff happened" ())))))
  (testing "Battle dialog parsing"
    (let [file (slurp "example_mixed_dialogs.json")]
      (testing "Should return battle dialog with simple nested win dialog"
        (is (= (di/parse-dialog-from-file file)
               (di/->BattleDialog "Battle" "With gremlin"
                                  (bt/->Person "Hero" 20 {:spear 10} {})
                                  (bt/->Person "Gremlin" 5 {:hands 1} {})
                                  (di/->SimpleDialog "next" "dialog" []))))))))
; TODO test no weapons enemy
; TODO test no equipment hero,enemy
(deftest simple-dialog-test
  (let [other_dialog (di/->SimpleDialog "Fuck the police" "other_dialog" [])
        dialog (di/->SimpleDialog "lulz" "wtf" [other_dialog])]

    (testing "Printing a dialog"
      (is (=
           (with-out-str (di/display dialog))
           "lulz\nwtf\n0:Fuck the police\n")))

    (testing "Choose dialog 0 should return first nested dialog"
      (is (= (di/choose dialog 0)  other_dialog)))))

(deftest battle-dialog-test
  (let [hero (bt/->Person "Hero" 10 {:spear 25} {})
        gremlin (bt/->Person "Hero" 5 {:gg 2} {})
        win-dialog (di/->SimpleDialog "f" "f" [])
        dialog (di/->BattleDialog "the battle" "" hero gremlin win-dialog)]
    (testing "Get-nth-weapon should return weapon name"
      (is (= (di/get-nth-weapon hero 0) :spear))
      (is (= (di/get-nth-weapon hero 1) 0)))
    (testing "Choose shouldn't return nill"
      (is (not (nil? (di/choose dialog 0)))))
    (testing "Display dialog should return battle status"
      (is (= (with-out-str (di/display dialog))
             "Battling with  Hero\nYour hp  10\nEnemy hp  5\nAttack with: \n\n1 : :spear   25  Damage\n")))))
