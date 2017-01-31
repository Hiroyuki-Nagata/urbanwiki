(ns wiki.view.default.default
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn default-header [title]
  [:head
   [:meta {:name "ROBOTS" :content "NOINDEX, NOFOLLOW"}]
   [:meta {:name "ROBOTS" :content "NOARCHIVE"}]
   [:meta {:http-equiv "Content-Type" :content "text/html; charset=UTF-8"}]
   [:meta {:http-equiv "Content-Style-Type" :content "text/css"}]
   [:link {:rel "stylesheet" :type "text/css" :href ""}]
   [:style {:type "text/css"}]
   [:title title]
   (include-css "/css/default/default.css")
   ])

(defn top [req & body]
  (html5
   (default-header "FreeStyleWiki - top")
   [:body
    [:header.top-bar.bg-green.depth-3 "FreeStyleWiki!"]
    [:main body]]))

(defn common [req]
  (html5
   (default-header "FreeStyleWiki - wiki")
   [:body
    [:header.top-bar.bg-green.depth-3 "FreeStyleWiki!"]]))
