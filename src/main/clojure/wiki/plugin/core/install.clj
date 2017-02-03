(ns wiki.plugin.core.install
  (:gen-class install true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j))

; TODO: wikiのインスタンスを渡す必要がある
(defn install []
  (set-logger!)
  (info "Install core plugin...")
  ;; トップページ
  )
  ;;(. wiki add-menu "トップ" (. wiki create-page-url (load-config :frontpage)) 999))
