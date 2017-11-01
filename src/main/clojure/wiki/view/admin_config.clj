(ns wiki.view.admin-config
  (:require [hiccup.page])
  (:use [hiccup.core]))

(defn admin-config-tmpl []
  (html
   "<!--=========================================================================-->"
   "<!-- 管理画面（環境設定） -->                                                   "
   "<!--=========================================================================-->"
   [:h2 "サイト情報"]

   [:h3 "サイト名"]
   [:p [:input {:type "text" :name "site_title" :size "40"}]]

   [:h3 "管理者の名前"]
   [:p [:input {:type "text" :name "admin_name" :size "40"}]]

   [:h3 "管理者のメールアドレス（メール送信用）"]
   [:p [:input {:type "text" :name "admin_mail" :size "40"}]
    "カンマで区切って複数記述できます。空欄にしておけば更新通知メールは送信されません。"]

   [:h3 "管理者のメールアドレス（公開用）"]
   [:p [:input {:type "text" :name "admin_mail_pub" :size "40"}]]

   [:h3 "更新通知メールの設定"]
   [:p "件名のプレフィックス"
    [:input {:type "text" :name "mail_prefix" :size "20"}]]

   [:p "表示する項目："
    [:input {:type "checkbox" :id "mail_id" :name "mail_id" :value "1"}]
    [:label {:for "mail_id"} "ユーザID（ログイン時のみ）"]

    [:input {:type "checkbox" :id "mail_remote_addr" :name "mail_remote_addr" :value "1"}]
    [:label {:for "mail_remote_addr"} "IPアドレス"]

    [:input {:type "checkbox" :id "mail_user_agent" :name "mail_user_agent" :value "1"}]
    [:label {:for "mail_user_agent"} "ユーザエージェント"]

    [:input {:type "checkbox" :id "mail_diff" :name "mail_diff" :value "1"}]
    [:label {:for "mail_diff"} "変更の差分"]

    [:input {:type "checkbox" :id "mail_diff" :name "mail_diff" :value "1"}]
    [:label {:for "mail_diff"} "変更の差分"]

    [:input {:type "checkbox" :id "mail_backup_source" :name "mail_backup_source" :value "1"}]
    [:label {:for "mail_backup_source"} "変更前のソース"]

    [:input {:type "checkbox" :id "mail_modified_source" :name "mail_modified_source" :value "1"}]
    [:label {:for "mail_modified_source"} "変更後のソース"]]


   ))
