;;
;; FSWikiのスタイル設定を行うアクションハンドラ
;;
(ns wiki.plugin.admin.admin-style-handler
  (:use
   [clojure.tools.logging]
   [html-template])
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]
            [clojure.java.io :as io]))

(def load-tmpl (io/resource "tmpl/admin_style.tmpl"))

(defn do-action [req]
  ;; Load tmplate file => string => print with using HTML-TEMPLATE
  (with-out-str (print-template (slurp load-tmpl) {})))
