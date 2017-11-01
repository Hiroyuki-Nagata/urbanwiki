(ns wiki.plugin.admin.admin-config-handler
  (:use
   clojure.tools.logging
   wiki.view.admin-config)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn do-action [req]
  (admin-config-tmpl))
