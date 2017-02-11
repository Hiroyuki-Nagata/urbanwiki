(ns wiki.wiki-test
  (:require [clojure.test :refer :all]
            [wiki.wiki :refer :all]
            [wiki.default-storage :refer :all]))

(deftest create-url-test
  (init-config)
  (testing "We should create wiki parameters without arguments"
    (is (= (load-config :script_name) (create-url))))
  (testing "We must create wiki parameters with arguments"
    (is (= (str (load-config :script_name) "?action=HOGE&type=1")
           (create-url {:action "HOGE" :type "1"}))))
  (testing "We also create wiki pages from a function"
    (is (= (str (load-config :script_name) "?page=FrontPage")
           (create-page-url "FrontPage")))))

(deftest add-menu-test
  (init-config)
  (testing "We should call add-menu"
    (add-menu "ログイン" "https://freestylewiki.xyz/fswiki/wiki.cgi?action=LOGIN" "999" "1"))
  (testing "We can call add-menu multi times"
    (add-menu "新規" "https://freestylewiki.xyz/fswiki/wiki.cgi?action=NEW" "998" "1"))
  (is (= 2 (count (load-config :menu)))))

(deftest add-hook-test
  (init-config)

  ;; wiki.plugin.core.handler-test
  ;; ここにテスト用のアクションハンドラを用意している
  (testing "We need to add hook scripts"
    (add-handler "TEST" "wiki.plugin.core.handler-test")
    (is (= (load-config :handler) [{"TEST" "wiki.plugin.core.handler-test"}])))

  ;; wiki.plugin.core.handler-test
  ;; 登録されたhandlerに対して呼び出しを行う
  (testing "We should call handler with call-handler"
    (let [response (call-handler "TEST")]
      (not (= nil response)))))
