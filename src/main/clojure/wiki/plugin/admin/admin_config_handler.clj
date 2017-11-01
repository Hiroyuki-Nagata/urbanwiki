(ns wiki.plugin.admin.admin-config-handler
  (:use
   [clojure.tools.logging]
   [html-template])
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]
            [clojure.java.io :as io]))

(def load-tmpl (io/resource "tmpl/admin_config.tmpl"))

(defn do-action [req]
  ;; Load tmplate file => string => print with using HTML-TEMPLATE
  (with-out-str (print-template (slurp load-tmpl) {})))
