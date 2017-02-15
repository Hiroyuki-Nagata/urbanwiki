(ns wiki.default-storage
  (:gen-class
   :main false)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [clojure.string :as str]
            [monger.core :as mg]
            [monger.credentials :as mcred]
            [immuconf.config :as cfg]))

;; Monger! return MongoDB's connection
(defn mongodb []
  (let [db   "urbanwiki"
        user (or (System/getenv "DATABASE_USER") "")
        pass (or (System/getenv "DATABASE_PASS") "")
        host (or (System/getenv "DATABASE_HOST") "127.0.0.1")
        port (or (System/getenv "DATABASE_PORT") "27017")]

    (if (or (str/blank? user) (str/blank? pass))
      ;; ユーザーやパスワードが設定されてないので開発環境
      (mg/connect (mg/server-address host port) (mg/mongo-options))
      ;; 本番環境
      (mg/connect-with-credentials (mg/server-address host port) (mg/mongo-options) (mcred/create user db pass)))
    ))

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
