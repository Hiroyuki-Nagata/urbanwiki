(ns wiki.plugin.core.source
  (:gen-class true)
  (:use
   clojure.tools.logging)
  (:require [clojure.string :as str]
            [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [req]
  (let [page-name (:page (wiki/params))]
    (wiki/get-page page-name)))

(defn hook [req]
  (wiki/add-menu "ソース" (wiki/create-url {:action "SOURCE" :page (:page (wiki/params))}) nil nil))
