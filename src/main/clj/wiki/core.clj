(ns wiki.core
  (:gen-class main true)
  (:require [ring.adapter.jetty :as server]))

(defonce server (atom nil))

;; ライブラリを使わないルーティング実装
(defn ok [body]
  {:status 200
   :body body})

(defn html [res]
  (assoc res :headers {"Content-Type" "text/html; charset=utf-8"}))

(defn not-found []
  {:status 404
   :body "<h1>404 page not found</1>"})

(defn home-view [req]
  "<h1>ホーム画面</h1>
   <a href=\"/wiki\">WIKI 一覧</a>")

(defn home [req]
  (-> (home-view req)
      ok
      html))

(def wiki-index
  [{:title "朝ごはんを作る"}
   {:title "燃えるゴミを出す"}
   {:title "卵を買って帰る"}
   {:title "お風呂を洗う"}])

(defn wiki-index-view [req]
  `("<h1>WIKI 一覧</h1>"
    "<ul>"
    ~@(for [{:keys [title]} wiki-index]
        (str "<li>" title "</li>"))
    "</ul>"))

(defn wiki-index [req]
  (-> (wiki-index-view req)
      ok
      html))

(def routes
  {"/" home
   "/wiki" wiki-index})

(defn match-route [uri]
  (get routes uri))

;(defn handler [req]
;  {:status 200
;   :headers {"Content-Type" "text/plain"}
;   :body "Hello, World!!"})

(defn handler [req]
  (let [uri (:uri req)
        maybe-fn (match-route uri)]
    (if maybe-fn
      (maybe-fn req)
      (not-found))))

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
