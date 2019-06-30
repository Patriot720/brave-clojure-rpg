(ns brave-clojure-rpg.dialog-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialog-controller :as di]
            [brave-clojure-rpg.battle :as bt]))

(let [other_dialog (di/->SimpleDialog "Fuck the police" "other_dialog" [])
      dialog (di/->SimpleDialog "lulz" "wtf" [other_dialog])]

  (deftest print-a-dialog
    (is (=
         (with-out-str (di/display dialog))
         "lulz\n1:Fuck the police\n")))

  (deftest choosing-dialog-variant-should-return-new-dialog
    (is (= (di/choose dialog 1)  other_dialog)))

  (deftest start-dialog-should-recursively-choose-dialogs
    (with-in-str "1" (is (= (di/start dialog) other_dialog)))))

(deftest parse-dialog-tree-from-file-test
  (let [file (slurp "example_dialog.json")]
    (is (= (di/parse-dialog-from-file file)
           (di/->SimpleDialog "Kill everyone" "You fucked up everyone"
                              [(di/->SimpleDialog "Wtf" "other stuff happened" ())])))))
(let [hero (bt/->Person "Hero" 10 {:spear 25} {})
      gremlin (bt/->Person "Hero" 5 {:gg 2} {})
      win-dialog (di/->SimpleDialog "f" "f" [])
      dialog (di/->BattleDialog "the battle" "" hero gremlin win-dialog)]
  (deftest get-hero-weapon-dmg-by-id-test
    (is (= (di/get-weapon-dmg-nth hero 0) :spear))
    (is (= (di/get-weapon-dmg-nth hero 1) 0)))
  (deftest battle-dialog-choice-test
    (is (not (nil? (di/choose dialog 0))))))
