(ns logoot-hoplon-castra.rpc
  (:require-macros
    [tailrecursion.javelin :refer [defc defc=]])
  (:require
   [tailrecursion.javelin :refer [cell]]
   [tailrecursion.castra :refer [mkremote]]))

(set! cljs.core/*print-fn* #(.log js/console %))

(defc state {:random nil})
(defc error nil)
(defc loading [])

(defc= random-number (get state :random))

(def get-state
  (mkremote 'logoot-hoplon-castra.api/get-state state error loading))

(defc recent-update {:changes [] :etag nil})

(defc= etag (get recent-update :etag ))

(def push-changes
  (mkremote 'logoot-hoplon-castra.api/push-changes (cell nil) error loading))

(def pull-changes
  (mkremote 'logoot-hoplon-castra.api/pull-changes recent-update error loading))

(def clear!
  (mkremote 'logoot-hoplon-castra.api/clear! recent-update error loading))

(defn init []
  (pull-changes @etag)
  (js/setInterval #(do (pull-changes @etag)) 1000))
