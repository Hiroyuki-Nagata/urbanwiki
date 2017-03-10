(ns wiki.plugin.admin.admin-plugin-handler
  (:use [clojure.tools.logging]
        [noir.response :only [redirect]])
  (:require [clojure.string :as str]
            [wiki.default-storage :as db]
            [wiki.wiki :as wiki]))

(defn list-plugins []
  ;; 可読性が悪い…が、まず "wiki.plugin" 以下の名前空間を取得している
  ;; その後、そこから "wiki.plugin.XXX" のうち "XXX" を取り出して重複を削除して返す
  (let [plugins (filter #(str/starts-with? (str (ns-name %)) "wiki.plugin.") (all-ns))
        plugin-ns (distinct (map #(nth (str/split (str %) #"\.") 2 nil) plugins))]
    (debug (pr-str "Installed plugin sources --> " plugins))
    (info  (pr-str "Installed plugin namespaces --> " plugin-ns))
    plugin-ns))

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
      [:th "説明"]]
     (for [plugin (list-plugins)]
       [:tr
        [:td
         [:input {:type "checkbox" :name "plugin" :value (str plugin)}]]
        [:td
         (str plugin)]
        [:td
         "説明"]])
     [:input {:type "submit" :name "save"   :value "保存"}]
     [:input {:type "reset"                 :value "リセット"}]
     [:input {:type "hidden" :name "action" :value "ADMINPLUGIN"}]]]])

(defn do-action [req]
  (if (some? (:save (wiki/params)))
    (save-plugin-config req)
    (plugin-config-form req)))
