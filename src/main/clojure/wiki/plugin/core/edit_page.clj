(ns wiki.plugin.core.edit-page
  (:gen-class true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j
   wiki.view.editform)
  (:require [clojure.string :as str]
            [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [wiki]
  (if (str/blank? (:save (wiki/params)))
    ;; 編集開始
    (let [page-name (:page (wiki/params))
          content (db/get-page page-name)]
      (editform-tmpl content "EDIT" "xxx" page-name))
    ;; 保存してそのページを見せる
    (let [page-name (:page (wiki/params))
          content (:content (wiki/params))]
      (db/save-page page-name content)
      (wiki/call-handler "SHOW"))))
