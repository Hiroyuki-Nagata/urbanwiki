(ns wiki.wiki-test
  (:require [clojure.test :refer :all]
            [wiki.wiki :refer :all]))

(deftest add-menu-test
  (testing "We should call add-menu"
    (add-menu "name" "href" "weight" "nofollow")))
