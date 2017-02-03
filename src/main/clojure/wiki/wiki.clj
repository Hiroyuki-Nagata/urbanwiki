(ns wiki.wiki
  (:gen-class
   :main false)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [compojure.core :refer [defroutes context GET]]
            [compojure.route :as route]
            [immuconf.config :as cfg]
            [wiki.view.default.default :as default]))

;;
;; Singleton pattern
;; http://mishadoff.com/blog/clojure-design-patterns/#singleton
;; http://stackoverflow.com/a/3268284/2565527
;;
;; USE:
;; https://github.com/levand/immuconf
;;
(def wiki-state (atom {}))
(defonce wiki-config-rsc "resources/config.edn")

(defn clear-state []
  (swap! wiki-state (fn [p] {})))

(defn get-state
  ([] @wiki-state)
  ([key] (@wiki-state key)))

(defn update-state [key val]
  (swap! wiki-state assoc key val))

(defn load-default-config []
  (cfg/load wiki-config-rsc))

;; グローバルなコンフィグ読み出し
(defn load-config
  ;; 引数なし
  ([] (let [conf (get-state)]
        (set-logger!)
        (debug (str "load-config: " conf))
        conf))
  ;; 引数あり
  ([& args] (let [conf (get-in (get-state) args)]
        (set-logger!)
        (debug (str "load-config: " conf))
        conf)))

;; グローバルなコンフィグ書き込み
(defn save-config [m]
  (set-logger!)
  (debug (str "save-config: " m))
  (let [updated (merge m (get-state))]
    (doseq [[k v] updated] (update-state k v))))

;; コンフィグの初期値を読み出す
(defn init-config []
  (set-logger!)
  (clear-state)
  (doseq [[k v] (cfg/load wiki-config-rsc)] (update-state k v))
  (debug (str "init-config: " @wiki-state))
  @wiki-state)

;; メニュー項目を追加します。既に同じ名前の項目が登録されている場合は上書きします。
;; 優先度が高いほど左側に表示されます。
;; add-menu(項目名,URL,優先度,クロールを拒否するかどうか)
(defn add-menu [name href weight nofollow]
  (set-logger!)
  (info (str "add-menu: name: " name ", href: " href ", weight: " weight ", nofollow: " nofollow)))

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
