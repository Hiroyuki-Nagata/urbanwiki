(ns wiki.plugin.core.show-page
  (:gen-class true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [clojure.string :refer [blank?]]
            [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [wiki]
  (let [page-name (:page (wiki/params))]
    (wiki/process-wiki (wiki/get-page page-name))))
