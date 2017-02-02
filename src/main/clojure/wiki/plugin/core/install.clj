(ns wiki.plugin.core.install
  (:gen-class install true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j))

; TODO: wikiのインスタンスを渡す必要がある
(defn install []
  ; 呼び出しイメージ
  ; (. wiki add-menu "トップ" "URL" 999)
  (set-logger!)
  (info "Install core plugin..."))
