(ns wiki.core
  (:gen-class main true)
  (:import (java.util.logging Logger Level))
  (:use
   clojure.tools.logging
   ring.middleware.cookies)
  (:require [environ.core :refer [env]]
            [compojure.core :refer [routes]]
            [ring.adapter.jetty :as server]
            [ring.util.response :as res]
            [ring.middleware.resource :refer [wrap-resource]]
            [wiki.plugin.core.install]
            [wiki.middleware :refer [wrap-dev]]
            [wiki.wiki :refer [wiki-routes]]
            [wiki.wiki :as wiki-instance]
            [wiki.default-storage :as db]))

(defonce server (atom nil))

(defn- wrap [handler middleware opt]
  ;; set cookie anyway
  ;; set middleware
  (if (true? opt)
    (middleware handler)
    (if opt
      (middleware handler opt)
      handler)))

(def app
  (-> (routes wiki-routes)
      (ring.middleware.cookies/wrap-cookies)
      wrap-dev))

(defn start-server [& {:keys [host port join?]
                       :or {host "localhost" port 3000 join? false}}]
  (let [port (if (string? port) (Integer/parseInt port) port)]
    (when-not @server
      (reset! server (server/run-jetty app {:host host :port port :join? join?})))))

(defn stop-server []
  (when @server
    (.stop @server)
    (reset! server nil)))

(defn restart-server []
  (when @server
    (stop-server)
    (start-server)))

(defonce wiki-instance (atom nil))

(defn -main [& {:as args}]
  ;; MongoDBのJUL由来のログを消してる
  (let [mongoLogger (Logger/getLogger "org.mongodb.driver")]
    (. mongoLogger setLevel(Level/SEVERE)))
  (info "Just a plain logging message, you should see the level at the beginning")

  ;; コンフィグを初期化
  (db/init-config)
  ;; MongoDB接続確認
  (if (db/mongodb-connected?)
    (info "Urbanwiki successfully connected with MongoDB!")
    (warn "Urbanwiki failed to connect MongoDB..."))
  ;; プラグインパッケージからinstall関数をテストで呼び出し
  (wiki.plugin.core.install/install @wiki-instance)

  (start-server
   :host (get args "host") :port (get args "port") :join? true))
