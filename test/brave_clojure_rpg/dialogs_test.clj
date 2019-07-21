(ns brave-clojure-rpg.dialogs-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialogs :as Dialogs]
            [brave-clojure-rpg.person :refer [->Person map->Person]]
            [brave-clojure-rpg.test-helpers :as helpers]))

(def simple-empty-dialog  (Dialogs/->SimpleDialog "Fuck the police" "simple-empty-dialog" (map->Person {}) []))

(deftest simple-dialog-test
  (let [dialog (Dialogs/->SimpleDialog "lulz" "wtf" (map->Person {}) [simple-empty-dialog])]

    (testing "Printing"
      (is (=
           (helpers/trim-carriage-return (with-out-str (Dialogs/display dialog)))
           "lulz\nwtf\n0:Fuck the police\n")))

    (testing "Choose dialog 0 should return first nested dialog"
      (is (= (Dialogs/choose dialog 0)  simple-empty-dialog)))

    (testing "choose dialog should return next dialog with HERO"
      (let [dialog (Dialogs/->SimpleDialog "lulz" "wtf" helpers/hero [simple-empty-dialog])]
        (is (= (:hero (Dialogs/choose dialog 0))
               helpers/hero))))))

(deftest side-effect-dialog-test
  (let [dialog (Dialogs/->SideEffectDialog "lulz" "wtf"
                                           helpers/empty-hero {} [simple-empty-dialog])
        damage {:damage 5}
        weapon {:weapon {:spear {:damage 10}}}]
    (testing "choose dialog 0 should damage hero"
      (let [dialog (update dialog :side-effects #(merge damage %))]
        (is (= (:hp (:hero (Dialogs/choose dialog 0))) 5))))
    (testing "acquiring weapons"
      (let [dialog (update dialog :side-effects #(merge weapon %))]
        (is (contains? (:equipment (:hero (Dialogs/choose dialog 0))) :spear))))
    (testing "multiple side-effects"
      (let [dialog (update dialog :side-effects #(merge damage weapon %))]
        (is (contains? (:equipment (:hero (Dialogs/choose dialog 0))) :spear))
        (is (= (:hp (:hero (Dialogs/choose dialog 0))) 5))))))

(deftest battle-dialog-test
  (let [hero (assoc helpers/empty-hero :weapons {:spear {:damage 25}})
        gremlin (assoc helpers/empty-enemy :weapons {:gg {:damage 2}})
        win-dialog (Dialogs/->SimpleDialog "f" "f" (map->Person {}) [])
        dialog (Dialogs/->BattleDialog "the battle" "" hero gremlin win-dialog)]

    (testing "Choose shouldn't return nill"
      (is (not (nil? (Dialogs/choose dialog 0)))))

    (testing "Printing"
      (is (= (helpers/trim-carriage-return (with-out-str (Dialogs/display dialog)))
             "Battling with  Gremlin\nYour hp  10\nEnemy hp  5\nAttack with: \n\n0 : :spear 25 Damage\n"))))

  (testing "go through whole losing BattleDialog"
    (let
     [dialog (Dialogs/->BattleDialog "" ""
                                     (->Person "Hero" 10 {:spear {:damage 5}} {} 10)
                                     (->Person "Gremlin" 25 {:g {:damage 5}} {} 50); TODO to helpers
                                     (Dialogs/->SimpleDialog "" "" (map->Person {}) []))
      expected (-> (Dialogs/choose dialog 0)
                   (Dialogs/choose 0))]
                   ; TODO passes not 100 percent of time (critical-hit)
      (is (= expected (Dialogs/->EmptyDialog))))))

(deftest condition-dialog-test
  (testing "If hero has milk"
    (let [dialog (Dialogs/->ConditionDialog "title" "win" "lose" helpers/hero :milk {:lul 25} simple-empty-dialog)]
      (testing "Doesn't have the milk - no spear"
        (is (not (contains? (:equipment (:hero (Dialogs/choose dialog 0))) :lul))))
      (testing "give spear" ; TODO different types of equipment with different effects
        (let [dialog (assoc-in dialog [:hero :equipment] {:milk 10})]
          (is (contains? (:equipment (:hero (Dialogs/choose dialog 0))) :lul)))))))
