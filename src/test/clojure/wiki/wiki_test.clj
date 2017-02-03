(ns wiki.wiki-test
  (:require [clojure.test :refer :all]
            [wiki.wiki :refer :all]))

(deftest load-config-test
  (testing "We should load config without arguments"
    (not (= nil (load-config))))
  (testing "We should load config with an argument"
    (not (= nil (load-config :menu))))
  (testing "We should load config for nested hashmap"
    (not (= nil (load-config :nest :test)))))

(deftest add-menu-test
  (testing "We should call add-menu"
    (add-menu "ログイン" "https://freestylewiki.xyz/fswiki/wiki.cgi?action=LOGIN" "999" "1")))
