(ns logoot-hoplon-castra.rpc
  (:require-macros
    [tailrecursion.javelin :refer [defc defc=]]
    ; [dly.core.floats :refer [mfloat=]]
    )
  (:require
   [tailrecursion.javelin]
   [tailrecursion.castra :refer [mkremote]]))

(defc state {:random nil})
(defc error nil)
(defc loading [])


(defc= random-number (get state :random))

(def get-state
  (mkremote 'logoot-hoplon-castra.api/get-state state error loading))

(defc etag nil)
(def push-changes
  (mkremote 'logoot-hoplon-castra.api/push-changes etag error loading))

(defn init []
  (get-state)
  (js/setInterval get-state 1000))

(defn float-test [] 
  ;(mfloat= 1.0 1.0)
  "float-test"
  )