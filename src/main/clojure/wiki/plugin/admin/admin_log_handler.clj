;;
;; ログファイルを管理するモジュール
;;
(ns wiki.plugin.admin.admin-log-handler
  (:use
   clojure.tools.logging)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [req]
  "Hello")
