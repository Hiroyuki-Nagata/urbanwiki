(ns wiki.view.login
  (:require [hiccup.page]))

(defn login-tmpl [url page]
  [:h2 "IDとパスワードを入力してください"]
  [:form {:action url :method "post"}
   [:table
    [:tr
     [:th "ID"]
     [:td
      [:input {:type "text" :name "id"}]]]
    [:tr
     [:th "Pass"]
     [:td [:input {:type "password" :name "pass"}]]]]
   [:br]
   [:input {:type "submit" :name "login" :value "ログイン"}]
   [:input {:type "hidden" :name "action" :value "LOGIN"}]
   [:input {:type "hidden" :name "page" :value page}]])
