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
    (testing "acquiring equipment"
      (let [dialog (update dialog :side-effects #(merge weapon %))]
        (is (contains? (:inventory (:hero (Dialogs/choose dialog 0))) :spear))))
    (testing "multiple side-effects"
      (let [dialog (update dialog :side-effects #(merge damage weapon %))]
        (is (contains? (:inventory (:hero (Dialogs/choose dialog 0))) :spear))
        (is (= (:hp (:hero (Dialogs/choose dialog 0))) 5))))))

(deftest battle-dialog-test
  (let [hero (assoc helpers/empty-hero :equipment {:weapon {:spear {:damage 25}}}) ; TODO change to equip api
        gremlin (assoc helpers/empty-enemy :equipment {:weapon {:gg {:damage 2}}})
        win-dialog (Dialogs/->SimpleDialog "f" "f" (map->Person {}) [])
        dialog (Dialogs/->BattleDialog "the battle" "" hero gremlin win-dialog)]

    (testing "Choose shouldn't return nill"
      (is (not (nil? (Dialogs/choose dialog 0)))))

    (testing "Printing"
      (is (= (helpers/trim-carriage-return (with-out-str (Dialogs/display dialog)))
             "Battling with  Gremlin\nYour hp  10\nEnemy hp  5\nAttack with: \n\n0 : :spear 25 Damage\n"))))

  (testing "go through whole losing BattleDialog"
    (let ; TODO test getting weapon with empty-hero
     [dialog (Dialogs/->BattleDialog "" "" ; TODO attack should'nt be 0 ever
                                     helpers/weak-enemy
                                     helpers/hero
                                     (Dialogs/->SimpleDialog "" "" (map->Person {}) []))
      expected (-> (Dialogs/choose dialog 0)
                   (Dialogs/choose 0))]
      (is (= expected (Dialogs/->EmptyDialog))))))

; (deftest gamemenu-dialog-test
;   (testing "Equipping equipment"
;     (is (= (Dialogs/choose 0 (Dialogs/->GameMenuDialog helpers/hero))
;            (Dialogs/->inventoryDialog helpers/hero)))))

(deftest condition-dialog-test
  (testing "If hero has milk"
    (let [dialog (Dialogs/->ConditionDialog "title" "win" "lose" helpers/hero :milk {:lul 25} simple-empty-dialog)]
      (testing "Doesn't have the milk - no spear"
        (is (not (contains? (:inventory (:hero (Dialogs/choose dialog 0))) :lul))))
      (testing "give spear" ; TODO different types of inventory with different effects
        (let [dialog (assoc-in dialog [:hero :inventory] {:milk 10})]
          (is (contains? (:inventory (:hero (Dialogs/choose dialog 0))) :lul)))))))
