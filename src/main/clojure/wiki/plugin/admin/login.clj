(ns wiki.plugin.admin.login
  (:gen-class true)
  (:use
   clojure.tools.logging)
  (:require [clojure.string :refer [blank?]]
            [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn default []
  (debug "Called login/default")
  "Hello")

(defn do-action [req]
  (debug "Called login/do-action")
  (default))
