(ns brave-clojure-rpg.dialog-test
  (:require [clojure.test :refer [testing deftest is]]
            [brave-clojure-rpg.dialog-controller :as di]))

(let [other_dialog (di/->SimpleDialog "Fuck the police" "other_dialog" [])
      dialog (di/->SimpleDialog "lulz" "wtf" [other_dialog])]

  (deftest print-a-dialog
    (is (=
         (with-out-str (di/print dialog))
         "lulz\r\n1:Fuck the police\r\n")))

  (deftest choosing-dialog-variant-should-return-new-dialog
    (is (= (di/choose dialog 1)  other_dialog)))

  (deftest start-dialog-should-recursively-choose-dialogs
    (with-in-str "1" (is (= (di/start dialog) other_dialog)))))

(deftest parse-dialog-tree-from-file-test
  (let [file (slurp "example_dialog.json")]
    (is (= (di/parse-dialog-from-file file)
           (di/->SimpleDialog "Kill everyone" "You fucked up everyone"
                              [di/->SimpleDialog "Wtf" "other stuff happened" ()])))))
