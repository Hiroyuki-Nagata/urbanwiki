(ns wiki.wiki
  (:require [compojure.core :refer [defroutes context GET]]
            [compojure.route :as route]))

(defn ok [body]
  {:status 200
   :body body})

(defn html [res]
  (assoc res :headers {"Content-Type" "text/html; charset=utf-8"}))

(defn not-found []
  {:status 404
   :body "<h1>404 page not found</1>"})

(defn home-view [req]
  "<h1>ホーム画面</h1>
   <a href=\"/wiki\">WIKI 一覧</a>")

(defn home [req]
  (-> (home-view req)
      ok
      html))

(def wiki-list
  ["朝ごはんを作る"
   "燃えるゴミを出す"
   "卵を買って帰る"
   "お風呂を洗う"])

(defn wiki-index-view [req]
  (clojure.string/join "\n"
                       ["<h1>WIKI</h1>"
                        "<ul>"
                        (clojure.string/join " " (map #(str "<li>" %1 "</li>") wiki-list))
                        "</ul>"]))

(defn wiki-index [req]
  (-> (wiki-index-view req)
      ok
      html))

;; compojureを使うルーティング実装
(defroutes handler
  (GET "/" req home)
  (GET "/wiki" req wiki-index)
  (route/not-found "<h1>404 page not found</h1>"))
