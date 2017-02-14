(ns wiki.wiki
  (:gen-class
   :main false)
  (:use
   clojure.walk
   clojure.tools.logging
   clj-logging-config.log4j
   clojure.test flatland.useful.utils
   wiki.html-parser)
  (:require [clojure.string :refer [blank?]]
            [compojure.core :refer [defroutes context GET]]
            [compojure.route :as route]
            [ring.util.codec :refer [form-encode form-decode]]
            [wiki.default-storage :as db]
            [wiki.view.default.default :as default]
            [wiki.view.default.header :as header]))

;; スレッドごとのwiki情報を管理する
(defonce wiki-initial-state {:title "" :edit 0 :params {}})
(def wiki-local-state (thread-local (atom wiki-initial-state)))

(defn clear-local-state []
  (swap! wiki-local-state (fn [p] wiki-initial-state)))

(defn get-local-state
  ([] @wiki-local-state)
  ([key] (@wiki-local-state key)))

(defn update-local-state [key val]
  (swap! @wiki-local-state assoc key val))

(defn update-each-state [m]
  (doseq [[k v] m] (update-local-state k v)))

(defn params []
  (:params @@wiki-local-state))

;; フックプラグインを登録します。登録したプラグインはdo-hookメソッドで呼び出します。
(defn add-hook [name obj]
  (db/append-config {:hook {:name name :obj obj}}))

;; add_hookメソッドで登録されたフックプラグインを実行します。
;; 引数にはフックの名前に加えて任意のパラメータを渡すことができます。
;; これらのパラメータは呼び出されるクラスのhookメソッドの引数として渡されます。
(defn do-hook [name]
  (for [class (:name (db/load-config :hook))]
    (let [s (str class "/hook")
          k (keyword s)
          ns-sym (symbol (namespace k))
          nm-sym (symbol (name k))]
      (debug (str "Calling namespace & func: " s))
      (require ns-sym)
      ((ns-resolve ns-sym nm-sym) nil))))

;; アクションハンドラプラグインを追加します。
;; リクエスト時にactionというパラメータが一致するアクションが呼び出されます。
(defn add-handler [action class]
  (let [new-handler (merge {action class} (db/load-config :handler))
        new-handler-permission (merge {action 1} (db/load-config :handler_permission))]
    (db/update-each-state {:handler new-handler})
    (db/update-each-state {:handler_permission new-handler-permission})))

;; add_handlerメソッドで登録されたアクションハンドラを実行します。
;; アクションハンドラのdo_actionメソッドの戻り値を返します。
(defn call-handler [action]
  (when (not (blank? (get (db/load-config :handler) action)))
    (let [n (get (db/load-config :handler) action)
          s (str n "/do-action")
          k (keyword s)
          ns-sym (symbol (namespace k))
          nm-sym (symbol (name k))]
      (debug (str "Calling namespace & func: " s))
      (require ns-sym)
      ;; threadごとのwikiの状態を渡す
      ((ns-resolve ns-sym nm-sym) @@wiki-local-state))))

;; 任意のURLを生成するためのユーティリティメソッドです。
;; 引数としてパラメータのハッシュリファレンスを渡します。
(defn create-url
  ([] (db/load-config :script_name))
  ([m] (str (db/load-config :script_name) "?" (form-encode m))))

;; ページにジャンプするためのURLを生成するユーティリティメソッドです。
;; 引数としてページ名を渡します。
(defn create-page-url [page]
  (create-url {:page page}))

;; メニュー項目を追加します。既に同じ名前の項目が登録されている場合は上書きします。
;; 優先度が高いほど左側に表示されます。
;; add-menu(項目名,URL,優先度,クロールを拒否するかどうか)
(defn add-menu [name href weight nofollow]
  (set-logger!)
  (db/append-config
   {:menu
    {:name name, :href href, :weight weight, :nofollow nofollow }})
  (info (str "add-menu: name: " name ", href: " href ", weight: " weight ", nofollow: " nofollow)))

;; アクションハンドラ中でタイトルを設定する場合に使用します。
;; 編集系の画面の場合、第二引数に1を指定してください。
;; ロボット対策用に以下のMETAタグが出力されます。
(defn set-title [arg & {:keys [title edit] :or {title "" edit 0}}]
  (update-local-state :title title)
  (update-local-state :edit edit))

(defn get-title []
  (get-local-state :title))

;; 引数で渡したWikiフォーマットの文字列をHTMLに変換して返します。
(defn process-wiki [source]
  (wiki.html-parser/parse source))

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

(defn wiki-index-view [req]
  (set-logger!)
  ;; パラメーターをチェック(Rubyみたいに)
  ;; actionがあればプラグインに対してcall-handlerして内容を受け取る
  (when (not (= 0 count (params)))
    (let [action (:action (params))
          contents (call-handler action)]
      (debug (str "action: " action))
      (debug (str "contents: " contents))))

  ;; プラグインを初期化
  ;; あまりadd-hookでinitializeを登録するプラグインがなさそう
  (do-hook "initialize")
  ;; メニューを取得
  (let [menus (db/load-config :menu) wiki-header (header/header-tmpl menus)]
    (debug (str "Get menu items: " (count menus)))
    (->> (default/common req wiki-header))))

(defn wiki-index [req]
  ;; 事前にこのスレッドにおけるクエリをparamsに格納
  (when (not (blank? (:query-string req)))
    (let [ps (keywordize-keys (form-decode (:query-string req)))]
      (debug (str "params: " ps))
      (update-local-state :params ps)))
  (-> (wiki-index-view req)
      ok
      html))

;; compojureを使うルーティング実装
(defroutes wiki-routes
  (GET "/" req wiki-index)
  (GET "/wiki.cgi" req wiki-index)
  (route/resources "/")
  (route/not-found "<h1>404 page not found</h1>"))
