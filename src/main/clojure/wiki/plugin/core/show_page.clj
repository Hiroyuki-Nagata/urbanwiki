(ns wiki.plugin.core.show-page
  (:gen-class true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j)
  (:require [clojure.string :refer [blank?]]
            [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [wiki]

  (let [pagename (if (blank? (get-in wiki [:params :page]))
                   (db/load-config :frontpage)
                   (get-in wiki [:params :page]))]

    ;; (. wiki set-title)
    ;; (. wiki do-hook "show")
    (wiki/process-wiki (wiki/get-page pagename))))
