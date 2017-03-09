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
  (wiki/add-handler "LOGIN" "wiki.plugin.admin.login")

  (wiki/add-admin-menu "環境設定"
                       (wiki/create-url {:action "ADMINCONFIG"})
                       999
                       "FSWiki全体の動作に関する設定を行います。")
  (wiki/add-admin-menu "スタイル設定"
                       (wiki/create-url {:action "ADMINSTYLE"})
                       998
                       "見栄えに関する設定を行います。")
  (wiki/add-admin-menu "ユーザ管理"
                       (wiki/create-url {:action "ADMINUSER"})
                       997
                       "ユーザの追加、変更、削除を行います。")
  (wiki/add-admin-menu "ページ管理"
                       (wiki/create-url {:action "ADMINPAGE"})
                       996
                       "ページの凍結、アクセス権限、一括削除を行います。")
  (wiki/add-admin-menu "削除されたページ"
                       (wiki/create-url {:action "ADMINDELETED"})
                       995
                       "削除されたページの確認と復元を行います。")
  (wiki/add-admin-menu "プラグイン設定"
                       (wiki/create-url {:action "ADMINPLUGIN"})
                       994
                       "プラグインの有効化、無効化を行います。")
  (wiki/add-admin-menu "ログ・キャッシュ"
                       (wiki/create-url { :action "ADMINLOG"})
                       992
                       "ログファイル、キャッシュファイルのダウンロードを削除を行います。")
  (wiki/add-admin-menu "スパム対策"
                       (wiki/create-url { :action "ADMINSPAM" })
                       991
                       "スパム対策用の設定を行います。"))
