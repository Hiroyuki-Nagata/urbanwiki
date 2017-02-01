(ns plugins.core.install
  ;(:gen-class install true)
  (:use
   clojure.tools.logging
   clj-logging-config.log4j))

(defn install []
  (set-logger!)
  (info "Install core plugin..."))
