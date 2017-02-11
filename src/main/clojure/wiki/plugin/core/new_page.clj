(ns wiki.plugin.core.new-page
  (:gen-class true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [wiki]
  "New page !")
