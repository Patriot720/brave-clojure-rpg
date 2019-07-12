(ns brave-clojure-rpg.helpers)

(defn trim-carriage-return [string]
  (clojure.string/replace string "\r" ""))
(defmacro nest-dialogs [x & dialogs]
  (let [x (map (fn [dialog] (concat dialog [[]])) x)]
    (loop [x x, dialogs dialogs]
      (if dialogs
        (let [dialog (first dialogs)
              threaded (if (vector? dialog)
                         (concat dialog [x])
                         (concat dialog [[x]]))]
          (recur threaded (next dialogs)))
        x))))

(defmacro ->
  "Threads the expr through the forms. Inserts x as the
  second item in the first form, making a list of it if it is not a
  list already. If there are more forms, inserts the first form as the
  second item in second form, etc."
  {:added "1.0"}
  [x & forms]
  (loop [x x, forms forms]
    (if forms
      (let [form (first forms)
            threaded (if (seq? form)
                       (with-meta `(~(first form) ~x ~@(next form)) (meta form))
                       (list form x))]
        (recur threaded (next forms)))
      x)))
