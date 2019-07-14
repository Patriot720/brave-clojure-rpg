(ns brave-clojure-rpg.dialogs-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialogs :as Dialogs]
            [brave-clojure-rpg.person :refer [->Person map->Person]]
            [brave-clojure-rpg.helpers :as helpers]))

(def simple-empty-dialog  (Dialogs/->SimpleDialog "Fuck the police" "simple-empty-dialog" (map->Person {}) []))
(def simple-hero (->Person "Hero" 20 {:spear 10} {}))

(deftest simple-dialog-test
  (let [dialog (Dialogs/->SimpleDialog "lulz" "wtf" (map->Person {}) [simple-empty-dialog])]

    (testing "Printing a dialog"
      (is (=
           (helpers/trim-carriage-return (with-out-str (Dialogs/display dialog)))
           "lulz\nwtf\n0:Fuck the police\n")))

    (testing "Choose dialog 0 should return first nested dialog"
      (is (= (Dialogs/choose dialog 0)  simple-empty-dialog)))

    (testing "Choosing dialog should return next dialog with HERO"
      (let [hero (->Person "Hero" 20 {:spear 10} {})
            dialog (Dialogs/->SimpleDialog "lulz" "wtf" hero [simple-empty-dialog])]
        (is (= (:hero (Dialogs/choose dialog 0))
               hero))))))

(deftest side-effect-dialog-test
  (testing "Dialog choice that does damage to hero"
    (let [dialog (Dialogs/->SideEffectDialog "lulz" "wtf"
                                             (->Person "Hero" 20 {} {}) 5 [simple-empty-dialog])]
      (is (= (:hp (:hero (Dialogs/choose dialog 0))) 15)))))

(deftest battle-dialog-test
  (let [hero (->Person "Hero" 10 {:spear 25} {})
        gremlin (->Person "Hero" 5 {:gg 2} {})
        win-dialog (Dialogs/->SimpleDialog "f" "f" (map->Person {}) [])
        dialog (Dialogs/->BattleDialog "the battle" "" hero gremlin win-dialog)]

    (testing "Choose shouldn't return nill"
      (is (not (nil? (Dialogs/choose dialog 0)))))

    (testing "Display dialog should return battle status"
      (is (= (helpers/trim-carriage-return (with-out-str (Dialogs/display dialog)))
             "Battling with  Hero\nYour hp  10\nEnemy hp  5\nAttack with: \n\n0 : :spear 25 Damage\n"))))

  (testing "Going through whole losing BattleDialog"
    (let
     [dialog (Dialogs/->BattleDialog "" ""
                                     (->Person "Hero" 10 {:spear 5} {})
                                     (->Person "Gremlin" 25 {:g 5} {})
                                     (Dialogs/->SimpleDialog "" "" (map->Person {}) []))
      expected (-> (Dialogs/choose dialog 0)
                   (Dialogs/choose 0))]
                   ; TODO not 100 percent of time true critical-hit
      (is (= expected (Dialogs/->EmptyDialog))))))

; TODO critical hit based on items

(deftest condition-dialog-test
  (testing "If hero has something"
    (let [dialog (Dialogs/->ConditionDialog "title" "win" "lose" simple-hero :milk {:lul 25} simple-empty-dialog)]
      (testing "Doesn't have the milk"
        (is (not (contains? (:equipment (:hero (Dialogs/choose dialog 0))) :lul))))
      (testing "Has a spear" ; TODO different types of equipment with different effects
        (let [dialog (assoc-in dialog [:hero :equipment] {:milk 10})]
          (is (contains? (:equipment (:hero (Dialogs/choose dialog 0))) :lul)))))))
