(ns wiki.plugin.core.new-page
  (:gen-class true)
  (:use
   clojure.tools.logging)
  (:require [hiccup.page]
            [wiki.wiki :as wiki]))

(defn do-action [req]
  [:form {:method "post" :action (wiki/create-url)}
   [:input {:type "text" :name "page" :size "40"}]
   [:input {:type "submit" :value "作成"}]
   [:input {:type "hidden" :name "action" :value "EDIT"}]])
