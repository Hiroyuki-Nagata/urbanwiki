(ns wiki.plugin.core.list-page
  (:gen-class true)
  (:use clojure.tools.logging)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [wiki]
  (wiki/set-title "ページの一覧")
  (let [list (db/get-page-list)]
    [:ul
     (for [li list]
       [:li
        [:a {:href (wiki/create-page-url (:page_name li))} (:page_name li)]])]))
