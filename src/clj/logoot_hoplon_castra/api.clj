(ns logoot-hoplon-castra.api
  (:require [tailrecursion.castra :refer [defrpc]]
            [logoot.document :refer [new-document merge-changes display]]
            [logoot.edn :as edn]))

(def document (atom (new-document)))

(defrpc push-changes [changes]
  (swap! document #(merge-changes % (edn/map->changes changes)))
  (println (display @document)))

(defrpc get-state []
  {:random (rand-int 100)})

