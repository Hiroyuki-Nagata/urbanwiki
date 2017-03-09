(ns wiki.plugin.admin.login
  (:gen-class true)
  (:use [clojure.tools.logging]
        [noir.response :only [redirect]]
        [wiki.view.login])
  (:require [clojure.string :refer [blank?]]
            [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn default [req]
  (debug "Called login/default")
  (let [page-name (:page (wiki/params))]
    (login-tmpl (wiki/create-url) page-name)))

(defn admin-form [login-info]
  ;;(if (= (:id login-info) :admin)
  (let [admin-menus (wiki/get-admin-menu)]
    [:div
     [:ul
      (for [menu admin-menus]
        [:li
         [:a {:href (:url menu)} (:label menu)] (str " - " (:desc menu))])]
     [:form {:action (wiki/create-url) :method "post"}
      [:input {:type "submit" :name "logout" :value "ログアウト"}]
      [:input {:type "hidden" :name "action" :value "LOGIN"}]]]))

(defn do-action [req]
  (debug "Called login/do-action")
  (if-let [login-info (wiki/get-login-info req)]
    ;; ログイン済み
    (admin-form login-info)
    ;; 未ログイン or これからログイン
    (let [params (wiki/params)
          id (or (:id params) "")
          pass (or (:pass params) "")
          page (or (:page params) "")]
      (if (wiki/login id pass)
        ;; ログイン成功
        (let [se (wiki/sessions)
              update-se (merge se {:wiki_id id})]
          (wiki/update-local-state :session update-se)
          (debug (str "Succeed to login, user: " id))
          (if (blank? page)
            "Succeed to login !"
            (redirect (wiki/create-page-url page))))
        ;; ログインしていないのでログインフォームへ
        (default req)))))
