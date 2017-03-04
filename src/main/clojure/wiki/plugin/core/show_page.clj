(ns wiki.plugin.core.show-page
  (:gen-class true)
  (:use
   clojure.tools.logging)
  (:require [clojure.string :refer [blank?]]
            [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [req]
  (debug "Called show-page/do-action")
  (wiki/do-hook "show" req)
  (let [page-name (:page (wiki/params))]
    (wiki/process-wiki (wiki/get-page page-name))))
