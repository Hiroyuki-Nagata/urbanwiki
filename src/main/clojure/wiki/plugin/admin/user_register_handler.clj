(ns wiki.plugin.admin.user-register-handler
  (:use
   clojure.tools.logging)
  (:require [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))
