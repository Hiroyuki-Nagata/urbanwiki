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
