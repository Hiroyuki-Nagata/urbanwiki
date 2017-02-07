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
  (wiki/add-menu "トップ" (wiki/create-page-url (db/load-config :frontpage)) 999 nil))
