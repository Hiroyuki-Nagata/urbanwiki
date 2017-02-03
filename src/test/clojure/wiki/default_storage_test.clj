(ns wiki.default-storage-test
  (:require [clojure.test :refer :all]
            [wiki.default-storage :refer :all]))

(deftest load-config-test
  (init-config)
  (testing "We should load config without arguments"
    (not (= nil (load-config))))
  (testing "We should load config with an argument"
    (not (= nil (load-config :menu))))
  (testing "We should load config for nested hashmap"
    (not (= nil (load-config :nest :test)))))

(deftest save-config-test
  (testing "We should save config with hashmap style"
    (init-config)
    (save-config {:result "success"})
    (load-config)
    (is (= "success" (load-config :result)))))

(deftest init-config-test
  (testing "We should load initial config at once"
    (is (= (load-default-config) (init-config)))))

(deftest clear-state-test
  (testing "We can clear wiki's state"
    (clear-state)
    (is (= {} (load-config)))))
