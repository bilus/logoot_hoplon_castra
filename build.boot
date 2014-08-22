#!/usr/bin/env boot

#tailrecursion.boot.core/version "2.5.1"

(set-env!
  :project      'logoot_hoplon_castra
  :version      "0.1.0-SNAPSHOT"
  :dependencies '[[tailrecursion/boot.task   "2.2.4"]
                  [tailrecursion/hoplon      "5.10.23"]
                  [com.keminglabs/cljx "0.4.0"]]
  :out-path     "resources/public"
  :src-paths    #{"src/hl" "src/cljs" "src/clj" "target/generated/src" "src/vendor"
                  "src/deps/logoot-generated/cljs" "src/deps/logoot-src/cljs"
                  "src/deps/logoot-generated/clj" "src/deps/logoot-src/clj"})

;; Static resources (css, images, etc.):
(add-sync! (get-env :out-path) #{"assets"})

(require '[tailrecursion.hoplon.boot :refer :all]
         '[tailrecursion.castra.task :as c]
         '[cljx.core :as cljx])

(deftask cljx
  []
  (cljx/cljx-compile [{:source-paths ["src/cljx"]
                  :output-path "target/generated/src/clj"
                  :rules :clj}
                 {:source-paths ["src/cljx"]
                  :output-path "target/generated/src/cljs"
                  :rules :cljs}
                 {:source-paths ["spec/cljx"]
                  :output-path "target/generated/spec/clj"
                  :rules :clj}
                 {:source-paths ["spec/cljx"]
                  :output-path "target/generated/spec/cljs"
                  :rules :cljs}])
  identity)

(deftask development
  "Build logoot_hoplon_castra for development."
  []
  (comp (watch)    
        (cljx)    
        (hoplon {:prerender false}) 
        (c/castra-dev-server 'logoot-hoplon-castra.api)))

(deftask dev-debug
  "Build logoot_hoplon_castra for development with source maps."
  []
  (comp (watch) (hoplon {:pretty-print true
                         :prerender false
                         :source-map true}) (c/castra-dev-server 'logoot-hoplon-castra.api)))

(deftask production
  "Build logoot_hoplon_castra for production."
  []
  (hoplon {:optimizations :advanced}))
