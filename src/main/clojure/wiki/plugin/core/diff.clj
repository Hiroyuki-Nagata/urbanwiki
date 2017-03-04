(ns wiki.plugin.core.diff
  (:gen-class true)
  (:use
   clojure.tools.logging)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [req]
  "Diff !")

(defn hook [req]
  (debug "Hello, Hook !!!???"))
