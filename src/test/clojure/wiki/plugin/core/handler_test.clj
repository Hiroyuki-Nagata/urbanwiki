(ns wiki.plugin.core.handler-test
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [clojure.test :refer :all]))

;;
;; プラグインのテスト用
;;

;; Wiki側でadd_handlerを使って登録された後
;; リクエストごとにクエリでactionを指定される
;; 実際に存在するactionが指定された場合これが呼ばれる
;; 基本的にテキストを返す
(defn do-action [wiki]
  "Hello, Hook system !")

;; wikiのインスタンスに対してなんらかの変更を行う
(defn hook [wiki]
  (set-logger!)
  (info "Called hook function..."))
