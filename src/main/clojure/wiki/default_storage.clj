(ns wiki.default-storage
  (:gen-class
   :main false)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [clojure.string :as str]
            [monger.collection :as mc]
            [monger.core :as mg]
            [monger.credentials :as mcred]
            [monger.operators :refer [$set]]
            [monger.conversion :refer [from-db-object]]
            [immuconf.config :as cfg]))

;; Monger! return MongoDB's connection
(defn mongodb-conn []
  (let [db   (or (System/getenv "DB_NAME") "urbanwiki")
        user (or (System/getenv "DB_USER") "")
        pass (or (System/getenv "DB_PASS") "")
        host (or (System/getenv "DB_HOST") "127.0.0.1")
        port (Integer/parseInt (or (System/getenv "DB_PORT") "27017"))]

    (if (or (str/blank? user) (str/blank? pass))
      ;; ユーザーやパスワードが設定されてないので開発環境
      (mg/connect {:host host :port port})
      ;; 本番環境
      (mg/connect-with-credentials (str host ":" port) (mcred/create user db pass))
    )))

(defn mongodb-connected? []
  (not (nil? (mongodb-conn))))

(defn get-page [page]
  (let [conn (mongodb-conn)
        db   (mg/get-db conn (or (System/getenv "DB_NAME") "urbanwiki"))
        coll "pages"
        contents (mc/find-maps db coll {:page_name page})]
    (debug contents)
    contents))

(defn save-page [page content]
  (let [conn (mongodb-conn)
        db   (mg/get-db conn (or (System/getenv "DB_NAME") "urbanwiki"))
        coll "pages"]
    ;; ページ名をキーにしてUPSERTを行う
    (mc/update db coll {:page_name page} {$set {:content content}} {:upsert true})))

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

(defn update-each-state [m]
  (doseq [[k v] m] (update-state k v)))

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

;; キーで指定されたハッシュマップに新たにコンフィグを追加
;; 適用できるのはベクタ型のみにしたい
(defn append-config [m]
  (set-logger!)
  (debug (str "append-config: " m))
  (doseq [[k v] m]
    (let [curvec (get-state k)
          conjed (conj curvec v)
          parent (hash-map k conjed)]
      (debug (str "current: " curvec))
      (debug (str "append : " v))
      (debug (str "after  : " parent))
      (update-each-state parent))))

;; コンフィグの初期値を読み出す
(defn init-config []
  (set-logger!)
  (clear-state)
  (doseq [[k v] (cfg/load wiki-config-rsc)] (update-state k v))
  (debug (str "init-config: " @wiki-state))
  @wiki-state)
