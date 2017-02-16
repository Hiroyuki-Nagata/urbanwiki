(ns wiki.view.editform
  (:require [hiccup.page]))

(defn editform-tmpl [content action last-modified page-name]
  "<!--=========================================================================-->"
  "<!-- ページの編集フォーム -->                                                   "
  "<!--=========================================================================-->"
  [:form.update {:method "post" :action "wiki.cgi"}
   [:textarea.edit {:cols "80" :rows "20" :name "content"} content]
   [:p
    [:input {:type "submit" :name "save"         :value " 保 存 "     }]
    [:input {:type "submit" :name "diff"         :value "差分を確認"  }]
    [:input {:type "submit" :name "preview"      :value "プレビュー"  }]
    [:input {:type "hidden" :name "action"       :value action        }]
    [:input {:type "hidden" :name "lastmodified" :value last-modified }]
    [:input {:type "hidden" :name "page"         :value page-name     }]]])
