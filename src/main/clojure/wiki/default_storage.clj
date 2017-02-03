(ns wiki.default-storage
  (:gen-class
   :main false)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [immuconf.config :as cfg]))

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

  ;;(doseq [[k v] m] (save-config (hash-map k (conj (load-config k) v)))))

;; キーで指定されたハッシュマップからコンフィグを削除
;;(defn remove-config [m]
;;  (set-logger!)
;;  (debug (str "remove-config: " m))
;;  (doseq [[k v] m] (conj (load-config k) v)))

;; コンフィグの初期値を読み出す
(defn init-config []
  (set-logger!)
  (clear-state)
  (doseq [[k v] (cfg/load wiki-config-rsc)] (update-state k v))
  (debug (str "init-config: " @wiki-state))
  @wiki-state)
