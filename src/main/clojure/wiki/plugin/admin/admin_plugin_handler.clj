(ns wiki.plugin.admin.admin-plugin-handler
  (:use [clojure.tools.logging]
        [noir.response :only [redirect]])
  (:require [clojure.string :as str]
            [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn save-plugin-config [req]
  (let [plugins (:plugin (wiki/params))]
    (db/save-config {:plugins plugins})
    (redirect (wiki/create-url {:action "ADMINPLUGIN"}))))

(defn plugin-config-form [req]
  [:div
   [:h2 "プラグインの設定"]
   [:form {:action (wiki/create-url) :method "post"}
    [:table
     [:tr
      [:th [:br]]
      [:th "プラグイン"]
      [:th "説明"]]]
    ;; <-- ここにプラグインのリスト取得処理を書く
    [:input {:type "submit" :name "save"   :value "保存"}]
    [:input {:type "reset"                 :value "リセット"}]
    [:input {:type "hidden" :name "action" :value "ADMINPLUGIN"}]]])

(defn do-action [req]
  (if (some? (:save (wiki/params)))
    (save-plugin-config req)
    (plugin-config-form req)))
