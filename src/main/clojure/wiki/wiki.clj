(ns wiki.wiki
  (:gen-class
   :main false)
  (:use [clojure.walk]
        [clojure.tools.logging]
        [flatland.useful.utils]
        [ring.middleware.session]
        [markdown.core]
        [noir.util.crypt :only [md5]]
        [wiki.html-parser])
  (:require [clojure.string :refer [blank?]]
            [compojure.core :refer [defroutes context GET POST]]
            [compojure.route :as route]
            [ring.util.codec :refer [form-encode form-decode]]
            [wiki.default-storage :as db]
            [wiki.view.default.default :as default]
            [wiki.view.default.header :as header]))

;;
;; スレッドごとのwiki情報を管理する
;;
(defonce wiki-initial-state {:title ""
                             :edit 0
                             :params {}
                             :session {}})

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

(defn has-value [key value]
  (fn [m]
    (= value (m key))))

(defn params []
  (:params @@wiki-local-state))

(defn sessions []
  (:session @@wiki-local-state))

;;
;; スレッドごとのwiki情報終わり
;; --------------------------------------------------------------------------------


;; フックプラグインを登録します。登録したプラグインはdo-hookメソッドで呼び出します。
(defn add-hook [name obj]
  (db/append-config {:hook {:name name :obj obj}}))

;; add_hookメソッドで登録されたフックプラグインを実行します。
;; 引数にはフックの名前に加えて任意のパラメータを渡すことができます。
;; これらのパラメータは呼び出されるクラスのhookメソッドの引数として渡されます。
(defn do-hook [name req]
  (debug (str "Callback name: " name))
  (doseq [class (db/load-config :hook)]
    (let [n (:obj class)
          s (str n "/hook")
          k (keyword s)
          ns-sym (symbol (namespace k))
          nm-sym (symbol "hook")]
      (require ns-sym)
      ((ns-resolve ns-sym nm-sym) req))))

(defn add-handler-base [action class type]
  (let [new-handler (merge {action class} (db/load-config :handler))
        new-handler-permission (merge {action type} (db/load-config :handler_permission))]
    (db/update-each-state {:handler new-handler})
    (db/update-each-state {:handler_permission new-handler-permission})))

;; アクションハンドラプラグインを追加します。
;; リクエスト時にactionというパラメータが一致するアクションが呼び出されます。
(defn add-handler [action class]
  (add-handler-base action class 1))

;; 管理者用のアクションハンドラを追加します。
;; このメソッドによって追加されたアクションハンドラは管理者としてログインしている場合のみ実行可能です。
;; それ以外の場合はエラーメッセージを表示します。
(defn add-admin-handler [action class]
  (add-handler-base action class 0))

;; add_handlerメソッドで登録されたアクションハンドラを実行します。
;; アクションハンドラのdo_actionメソッドの戻り値を返します。
(defn call-handler [action req]
  (when (not (blank? (get (db/load-config :handler) action)))
    (let [n (get (db/load-config :handler) action)
          s (str n "/do-action")
          k (keyword s)
          ns-sym (symbol (namespace k))
          nm-sym (symbol (name k))]
      (debug (str "Calling namespace & func: " s))
      (require ns-sym)
      ;; threadごとのwikiの状態を渡す
      ((ns-resolve ns-sym nm-sym) req))))

;; 任意のURLを生成するためのユーティリティメソッドです。
;; 引数としてパラメータのハッシュリファレンスを渡します。
(defn create-url
  ([] (db/load-config :script_name))
  ([m]
   (let [script-name (db/load-config :script_name)]
     (if (some? m)
       ;; wiki.cgi?action=xxx ==> valueの値にnilがあったら消してる
       (str script-name "?" (form-encode (into {} (filter (comp some? val) m))))
       ;; wiki.cgi
       script-name))))

;; ページにジャンプするためのURLを生成するユーティリティメソッドです。
;; 引数としてページ名を渡します。
(defn create-page-url [page]
  (create-url {:page page}))

;; メニュー項目を追加します。既に同じ名前の項目が登録されている場合は上書きします。
;; 優先度が高いほど左側に表示されます。
;; add-menu(項目名,URL,優先度,クロールを拒否するかどうか)
;; TODO: この機能はセッションを利用しないとすぐに仕組みが崩壊するので後々改修する
(defn add-menu [name href weight nofollow]
  (if (empty? (filter (has-value :name name) (db/load-config :menu)))
    ;; キーがなければそのまま追加
    (db/append-config
     {:menu
      {:name name :href href :weight weight :nofollow nofollow }})
    ;; あればキーで更新する
    (db/update-config-with-key
     {:menu
      {:name name :href href :weight weight :nofollow nofollow }} :name name))
  (info (str "add-menu: result: " (pr-str (filter (has-value :name name) (db/load-config :menu))))))

;; 管理者用のメニューを追加します。管理者ユーザがログインした場合に表示されます。
;; 優先度が高いほど上のほうに表示されます。
;; TODO: この機能はセッションを利用しないとすぐに仕組みが崩壊するので後々改修する
(defn add-admin-menu [label url weight desc]
  (if (empty? (filter (has-value :label label) (db/load-config :admin_menu)))
    ;; キーがなければそのまま追加
    (db/append-config
     {:admin_menu
      {:label label :url url :weight weight :desc desc :type 0 }})
    ;; あればキーで更新する
    (db/update-config-with-key
     {:admin_menu
      {:label label :url url :weight weight :desc desc :type 0 }} :label label))
  (info (str "add-admin-menu: result: " (pr-str (filter (has-value :label label) (db/load-config :admin_menu))))))

;; アクションハンドラ中でタイトルを設定する場合に使用します。
;; 編集系の画面の場合、第二引数に1を指定してください。
;; ロボット対策用に以下のMETAタグが出力されます。
(defn set-title [arg & {:keys [title edit] :or {title "" edit 0}}]
  (update-local-state :title title)
  (update-local-state :edit edit))

(defn get-title []
  (get-local-state :title))

;; ユーザーをメモリDBに追加
(defn add-user [id pass type]
  (db/append-config
   {:user
    {:id id :pass (md5 pass id) :type type}}))

;; ログインチェック
(defn login [id pass]
  (= 1 (count
        (filter (and #(= (:id %) id)
                     #(= (:pass %) (md5 pass id)))
                (db/load-config :user)))))

;; 引数で渡したWikiフォーマットの文字列をHTMLに変換して返します。
;; TODO: とりあえず今はMaekdownのみ対応
(defn process-wiki [source]
  (md-to-html-string source))

;; ページのソースを取得します。
;; 第三引数にフォーマット名を渡した場合のみ、フォーマットプラグインによる
;; ソースの変換を行います。それ以外の場合は必要に応じてプラグイン側で
;; Wiki::convert_from_fswikiメソッドを呼んで変換を行います。
(defn get-page [page]
  (let [content (db/get-page page)]
    (debug (:content (first content)))
    (:content (first content))))

(defn ok [body req]
  (debug (str "session value: " (sessions) " params: " (:page (params)) " cookies: " (:cookies req)))
  {:status 200
   :session (merge (sessions) {:page (:page (params))})
   :body body })

(defn html [res]
  (assoc res :headers {"Content-Type" "text/html; charset=utf-8"
                       "Pragma" "no-cache"
                       "Cache-Control" "no-cache"}))

(defn not-found []
  {:status 404
   :body "<h1>404 page not found</1>"})

(defn wiki-index-view [req]
  ;; プラグインを初期化
  (do-hook "initialize" nil)
  ;; メニューを取得
  (let [menus (db/load-config :menu)
        wiki-header (header/header-tmpl menus)
        action (or (:action (params)) "SHOW")
        page (:page (params))
        contents (call-handler action req)]
    (debug (str "Get menu items: " menus))
    (debug (str "Action        : " action))
    (debug (str "Contents      : " contents))
    (->> (default/common req wiki-header contents))))

(defn wiki-index [req]
  ;; GETの場合, 事前にこのスレッドにおけるクエリをparamsに格納
  (when (not (blank? (:query-string req)))
    (let [ps (keywordize-keys (form-decode (:query-string req)))
          se (:session req)]
      (debug (str "query-params: " ps))
      (update-local-state :params ps)
      (update-local-state :session se)))
  (-> (wiki-index-view req)
      (ok req)
      html))

(defn wiki-action [req]
  ;; POSTの場合
  (let [body (slurp (:body req) :encoding "utf-8")
        ps (keywordize-keys (form-decode body "utf-8"))
        se (:session req)]
    (debug (str "form-params: " ps))
    (update-local-state :params ps)
    (update-local-state :session se))
  (-> (wiki-index-view req)
      (ok req)
      html))

;; compojureを使うルーティング実装
(defroutes wiki-routes
  (GET  "/" req wiki-index)
  (GET  "/wiki.cgi" req wiki-index)
  (POST "/wiki.cgi" req wiki-action)
  (route/resources "/")
  (route/not-found "<h1>404 page not found</h1>"))
