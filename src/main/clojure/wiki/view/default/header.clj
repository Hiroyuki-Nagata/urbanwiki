(ns wiki.view.default.header
  (:require [hiccup.page]))

(defn header-tmpl [menu-list]
  "<!--========================================================================-->"
  "<!-- ヘッダ（メニューを表示） -->                                              "
  "<!--========================================================================-->"
  [:div.adminmenu
   (for [menu menu-list]
     [:span.adminmenu
      [:a {:href (:href menu) :rel  (:rel menu)} (:name menu)]])])
