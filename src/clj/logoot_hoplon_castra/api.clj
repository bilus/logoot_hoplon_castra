(ns logoot-hoplon-castra.api
  (:require [tailrecursion.castra :refer [defrpc]]
            [logoot.document :refer [new-document display]]
            [logoot.merge :as m]
            [logoot.edn :as edn]))

(def document (atom (new-document)))
(def change-history (atom []))

(defrpc push-changes [changes] 
  {:rpc/query ["ok"]}
  (println "push-changes/changes = " changes)
  (swap! document #(first (m/merge-changes % (edn/map->changes changes))))
  (println "push-changes/document = " (display @document))
  (swap! change-history #(conj % (edn/map->changes changes))))

(defrpc pull-changes [etag]
  (let [good-etag (or etag 0)
        changes (drop good-etag @change-history)
        etag' (+ good-etag (count changes))]
    (when-not (empty? changes)
      (println "pull-changes/result  =" {:etag etag' :changes changes}))
    {:etag etag' :changes (map edn/changes->map changes)}))

(defrpc clear! 
  []
  (println "clear!")
  (reset! document (new-document))
  (reset! change-history [])
  {:etag 0 :changes []})

(defrpc get-state []
  {:random (rand-int 100)})

