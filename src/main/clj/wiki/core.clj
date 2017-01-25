(ns wiki.core
  (:gen-class main true)
  (:require [ring.adapter.jetty :as server]))

(defonce server (atom nil))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello, World!!"})

(defn start-server [& {:keys [host port join?]
                       :or {host "localhost" port 3000 join? false}}]
  (let [port (if (string? port) (Integer/parseInt port) port)]
    (when-not @server
      (reset! server (server/run-jetty handler {:host host :port port :join? join?})))))

(defn stop-server []
  (when @server
    (.stop @server)
    (reset! server nil)))

(defn restart-server []
  (when @server
    (stop-server)
    (start-server)))

(defn -main [& {:as args}]
  (start-server
   :host (get args "host") :port (get args "port") :join? true))
