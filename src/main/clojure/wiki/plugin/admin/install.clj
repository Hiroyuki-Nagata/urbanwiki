;;
;; ログイン機能、管理画面を提供します
;;
(ns wiki.plugin.admin.install
  (:gen-class install true)
  (:use
   clojure.tools.logging)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn install []
  (info "Install admin plugin...")
  (wiki/add-menu "ログイン" (wiki/create-url {:action "LOGIN"}) 0 nil)
  (wiki/add-handler "LOGIN" "wiki.plugin.admin.login"))
