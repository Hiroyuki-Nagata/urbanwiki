(ns wiki.plugin.core.diff
  (:gen-class true)
  (:use
   clojure.tools.logging)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [wiki]
  "Diff !")

(defn hook []
  (debug "Hello, Hook !!!???"))
