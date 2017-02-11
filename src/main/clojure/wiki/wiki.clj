(ns wiki.wiki
  (:gen-class
   :main false)
  (:use
   clojure.walk
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [compojure.core :refer [defroutes context GET]]
            [compojure.route :as route]
            [ring.util.codec :refer [form-encode form-decode]]
            [wiki.default-storage :as db]
            [wiki.view.default.default :as default]
            [wiki.view.default.header :as header]))

;; プラグインのインスタンスを取得します。wiki.cljで内部的に使用されるメソッドです。
;; プラグイン開発において通常、このメソッドを使用する必要はありません。
(defn get-plugin-instance [class]
  "")

;; フックプラグインを登録します。登録したプラグインはdo-hookメソッドで呼び出します。
(defn add-hook [name obj]
  (db/append-config {:hook {:name name :obj obj}}))

;; add_hookメソッドで登録されたフックプラグインを実行します。
;; 引数にはフックの名前に加えて任意のパラメータを渡すことができます。
;; これらのパラメータは呼び出されるクラスのhookメソッドの引数として渡されます。
(defn do-hook [name]
  (for [class (:name (db/load-config :hook))]
    (let [obj (get-plugin-instance class)]
      (. obj hook)))) ;; TODO: 必要ならここに可変長引数

;; アクションハンドラプラグインを追加します。
;; リクエスト時にactionというパラメータが一致するアクションが呼び出されます。
(defn add-handler [action class]
  (db/append-config {:handler {action class}})
  (db/append-config {:handler_permission {:action 1}}))

;; add_handlerメソッドで登録されたアクションハンドラを実行します。
;; アクションハンドラのdo_actionメソッドの戻り値を返します。
(defn call-handler [action]
  ;; my $obj = $self->get_plugin_instance($self->{"handler"}->{$action});
  )

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
  (let [params (keywordize-keys (form-decode (:query-string req)))
        action (:action params)
        contents (call-handler action)]
    (debug (str "action: " action))
    (debug (str "contents: " contents)))

  ;; プラグインを初期化
  ;; あまりadd-hookでinitializeを登録するプラグインがなさそう
  (do-hook "initialize")
  ;; メニューを取得
  (let [menus (db/load-config :menu) wiki-header (header/header-tmpl menus)]

    (debug (str "Get menu items: " (count menus)))
    (->> (default/common req wiki-header))))

(defn wiki-index [req]
  (-> (wiki-index-view req)
      ok
      html))

;; compojureを使うルーティング実装
(defroutes wiki-routes
  (GET "/" req wiki-index)
  (GET "/wiki.cgi" req wiki-index)
  (route/resources "/")
  (route/not-found "<h1>404 page not found</h1>"))
