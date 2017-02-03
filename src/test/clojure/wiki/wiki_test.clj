(ns wiki.wiki-test
  (:require [clojure.test :refer :all]
            [wiki.wiki :refer :all]
            [wiki.default-storage :refer :all]))

(deftest add-menu-test
  (init-config)
  (testing "We should call add-menu"
    (add-menu "ログイン" "https://freestylewiki.xyz/fswiki/wiki.cgi?action=LOGIN" "999" "1"))
  (testing "We can call add-menu multi times"
    (add-menu "新規" "https://freestylewiki.xyz/fswiki/wiki.cgi?action=NEW" "998" "1"))
  (is (= 2 (count (load-config :menu)))))
