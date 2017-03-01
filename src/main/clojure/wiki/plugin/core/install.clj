;;
;; FreeStyleWikiの基本的な機能を提供します。
;;
(ns wiki.plugin.core.install
  (:gen-class install true)
  (:use
   clojure.tools.logging)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn install [wiki]
  (info "Install core plugin...")
  ;; トップページ
  (wiki/add-menu "トップ" (wiki/create-page-url (db/load-config :frontpage)) 999 nil)
  (wiki/add-menu "新規"   (wiki/create-url {:action "NEW"})                  998 1)
  (wiki/add-menu "編集"   (wiki/create-url {:action "EDIT"})                 997 1)
  (wiki/add-menu "一覧"   (wiki/create-url {:action "LIST"})                 995 nil)
  (wiki/add-menu "ヘルプ" (wiki/create-page-url "Help")                      100 nil)

  (wiki/add-handler "SHOW" "wiki.plugin.core.show-page")
  (wiki/add-handler "NEW"  "wiki.plugin.core.new-page" )
  (wiki/add-handler "LIST" "wiki.plugin.core.list-page")
  (wiki/add-handler "EDIT" "wiki.plugin.core.edit-page")
  (wiki/add-hook    "show" "wiki.plugin.core.edit-page")
  (wiki/add-handler "DIFF" "wiki.plugin.core.diff"     )
  (wiki/add-hook    "show" "wiki.plugin.core.diff"     ))
