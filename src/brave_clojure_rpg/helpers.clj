(ns brave-clojure-rpg.helpers
  (:gen-class))
; (cond
;         (person/dead? damaged-enemy) (pass-hero-to-next-dialog win-dialog hero)
;         (person/dead? damaged-hero) (->EmptyDialog)
;         :else (->BattleDialog
;                title description
;                damaged-hero
;                damaged-enemy
;                win-dialog))

; (condt person/dead?
;         damaged-enemy (pass-hero-to-next-dialog win-dialog hero)
;         damaged-hero (->EmptyDialog)
;         (->BattleDialog
;                title description
;                damaged-hero
;                damaged-enemy
;                win-dialog))
(defn condt-f [f result clauses]
  (case (count clauses)
    1 (concat result (list true (first clauses)))
    0 result
    (recur f (concat result (list (list f (first clauses)) (nth clauses 1)))
           (next (next clauses)))))

(defmacro condt
  "Thread clauses through f"
  [f & clauses]
  `(cond ~@(condt-f f () clauses)))
