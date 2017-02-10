(ns wiki.plugin.core.install
  (:gen-class install true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

; TODO: wikiのインスタンスを渡す必要がある
(defn install [wiki]
  (set-logger!)
  (info "Install core plugin...")
  ;; トップページ
  (wiki/add-menu "トップ" (wiki/create-page-url (db/load-config :frontpage)) 999 nil)
  (wiki/add-menu "新規"   (wiki/create-url {:action "NEW"})                  998 1)
  (wiki/add-menu "編集"   (wiki/create-url {:action ""})                     997 1)
  (wiki/add-menu "一覧"   (wiki/create-url {:action "LIST"})                 995 nil)
  (wiki/add-menu "ヘルプ" (wiki/create-page-url "Help")                      100 nil))
