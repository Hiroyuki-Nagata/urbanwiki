(ns wiki.core
  (:gen-class main true)
  (:import [java.util.logging Logger Level]
           [de.bwaldvogel.mongo MongoServer]
           [de.bwaldvogel.mongo.backend.h2 H2Backend])
  (:use [clojure.tools.logging]
        [ring.middleware.session]
        [wiki.wiki :refer [wiki-routes]])
  (:require [environ.core :refer [env]]
            [compojure.core :refer [routes]]
            [ring.adapter.jetty :as server]
            [ring.util.response :as res]
            [ring.middleware.resource :refer [wrap-resource]]
            [wiki.middleware :refer [wrap-dev]]
            ;;[wiki.wiki :refer [wiki-routes]]
            [wiki.default-storage :as db]
            [wiki.plugin.core.install]
            [wiki.plugin.admin.install]
            ))

(defonce server (atom nil))

(defn- wrap [handler middleware opt]
  ;; set middleware
  (if (true? opt)
    (middleware handler)
    (if opt
      (middleware handler opt)
      handler)))

(def app
  (-> (routes wiki-routes)
      (ring.middleware.session/wrap-session)
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

(defn start-mongodb-h2 []
  (let [mongo (new MongoServer(new H2Backend "database.mv"))]
    (info "Prepare file-based MongoDB !!!")
    (. mongo bind "localhost" 27017)
    (info "Start file-based MongoDB !!!")))

(defonce wiki-instance (atom nil))

(defn -main [& {:as args}]
  ;; MongoDBのJUL由来のログを消してる
  (let [mongoLogger (Logger/getLogger "org.mongodb.driver")]
    (. mongoLogger setLevel(Level/SEVERE)))
  (info "Just a plain logging message, you should see the level at the beginning")

  ;; コンフィグを初期化
  (db/init-config)
  ;; 初期ユーザーを登録
  (wiki.wiki/add-user "admin" "admin" :admin)
  (wiki.wiki/add-user "user" "user" :user)

  ;; 開発環境かどうか？
  (if (db/mongodb-is-dev?)
    (start-mongodb-h2))

  ;; MongoDB接続確認
  (if (db/mongodb-connected?)
    (info "Urbanwiki successfully connected with MongoDB!")
    (warn "Urbanwiki failed to connect MongoDB..."))
  ;; プラグインパッケージからinstall関数をテストで呼び出し
  (wiki.plugin.core.install/install)
  (wiki.plugin.admin.install/install)

  (start-server
   :host (get args "host") :port (get args "port") :join? true))
