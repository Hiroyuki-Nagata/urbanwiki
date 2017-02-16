(ns wiki.plugin.core.edit-page
  (:gen-class true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j
   wiki.view.editform)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [wiki]
  ;; TODO: いろいろやることがあるぞ…
  (editform-tmpl "ほげ" "Yeah" "??" "sample-name"))
