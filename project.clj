(defproject wiki "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :uberjar-name "wiki-clj.jar"
  :min-lein-version "2.5.3"
  :dependencies [
                 [clj-http/clj-http "2.3.0"]
                 [clj-logging-config/clj-logging-config "1.9.12"]
                 [compojure/compojure "1.4.0"]
                 [environ/environ "1.0.3"]
                 [hickory/hickory "0.7.0"]
                 [levand/immuconf "0.1.0"]
                 [org.flatland/useful "0.11.5"]
                 [org.clojure/clojure "1.8.0"]
                 [prone/prone "0.8.2"]
                 [ring/ring "1.4.0"]
                 ]
  :source-paths ["src/main/clojure"]
  :test-paths ["src/test/clojure"]
  :java-source-paths ["src/main/java"]
  :pom-plugins [[com.theoryinpractise/clojure-maven-plugin "1.3.8"
                 {:configuration ([:mainClass "wiki.core"]
                                  [:sourceDirectories [:sourceDirectory "src/main/clojure"]]
                                  [:testSourceDirectories [:testSourceDirectory "src/test/clojure"]]
                                  [:args "host localhost port 3000"])}
                 ]
                [org.apache.maven.plugins/maven-shade-plugin "2.4.3"
                 [:executions [:execution ([:phase "package"]
                                           [:goals [:goal "shade"]]
                                           [:configuration
                                            [:transformers
                                             [:transformer
                                              {:implementation "org.apache.maven.plugins.shade.resource.ApacheLicenseResourceTransformer"}]
                                             [:transformer
                                              {:implementation "org.apache.maven.plugins.shade.resource.ApacheNoticeResourceTransformer"}]
                                             [:transformer
                                              {:implementation "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer"}
                                              [:mainClass "wiki.core"]]
                                             ]
                                            [:finalName "wiki-clj"] ;"${project.artifactId}-${project.version}-standalone"]
                                            [:filters
                                             [:filter
                                              [:includes
                                               [:include "**/*.js"]
                                               [:include "**/*.class"]
                                               [:include "**/*.xml"]]
                                              [:excludes
                                               [:exclude "**/*.clj"]]]]])]]]]
  :resource-paths ["resources"]
  :profiles
  {:dev {:env {:dev true }}
   :uberjar {:aot :all
             :main wiki.core}})
