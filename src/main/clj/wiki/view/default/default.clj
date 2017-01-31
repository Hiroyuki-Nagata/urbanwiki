(ns wiki.view.default.default
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn top [req & body]
  (html5
   [:head
    [:title "FreeStyleWiki - Sample"]]
   [:body
    [:header.top-bar.bg-green.depth-3 "FreeStyleWiki!"]
    [:main body]]))

(defn common [req]
  (html5
   [:head
    [:title "FreeStyleWiki - Sample"]]
   [:body
    [:header.top-bar.bg-green.depth-3 "FreeStyleWiki!"]]))
