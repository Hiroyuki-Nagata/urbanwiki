(ns wiki.wiki
  (:require [compojure.core :refer [defroutes context GET]]
            [compojure.route :as route]
            [wiki.view.default.default :as default]))

(defn ok [body]
  {:status 200
   :body body})

(defn html [res]
  (assoc res :headers {"Content-Type" "text/html; charset=utf-8"
                       "Pragma" "no-cache"
                       "Cache-Control" "no-cache"}))

(defn not-found []
  {:status 404
   :body "<h1>404 page not found</1>"})

(defn home-view [req]
  (->> [:section.card
        [:h2 "ホーム画面"]
        [:a {:href "/wiki"} "WIKI 一覧"]]
       (default/top req)))

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
  (->> (default/common req)))

(defn wiki-index [req]
  (-> (wiki-index-view req)
      ok
      html))

;; compojureを使うルーティング実装
(defroutes wiki-routes
  (GET "/" req home)
  (GET "/wiki" req wiki-index)
  (route/resources "/")
  (route/not-found "<h1>404 page not found</h1>"))
