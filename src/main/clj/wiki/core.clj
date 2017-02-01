(ns wiki.core
  (:gen-class main true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [environ.core :refer [env]]
            [compojure.core :refer [routes]]
            [ring.adapter.jetty :as server]
            [ring.adapter.jetty :as server]
            [ring.util.response :as res]
            [wiki.middleware :refer [wrap-dev]]
            [wiki.wiki :refer [wiki-routes]]
            [ring.middleware.resource :refer [wrap-resource]]))

(defonce server (atom nil))

(defn- wrap [handler middleware opt]
  (if (true? opt)
    (middleware handler)
    (if opt
      (middleware handler opt)
      handler)))

(def app
  (-> (routes
       wiki-routes)
      (wrap-resource "src/main/resources")
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

(defn -main [& {:as args}]
  (set-logger!)
  (info "Just a plain logging message, you should see the level at the beginning")

  ;; プラグインパッケージからinstall関数をテストで呼び出し
  (require 'plugins.core.install)
  (println (keys (ns-publics 'plugins.core.install)))
  (plugins.core.install/install)

  (start-server
   :host (get args "host") :port (get args "port") :join? true))
