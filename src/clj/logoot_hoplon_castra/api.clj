(ns logoot-hoplon-castra.api
  (:require [tailrecursion.castra :refer [defrpc]]
            [logoot.document :refer [new-document display valid?]]
            [logoot.merge :as m]
            [logoot.edn :as edn]))

; (def default-document (apply sorted-map (read-string "[#logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 0, :site 0}], :clock nil} \">\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 856813662, :site 1}], :clock 0} \"a\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 1421322593, :site 2}), :clock 1} \"b\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 1449885261, :site 1}], :clock 2} \"c\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 1904040886, :site 1}], :clock 3} \"d\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 1911496212, :site 2}), :clock 4} \"e\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2095803689, :site 1}], :clock 5} \"f\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2098376426, :site 2}), :clock 6} \"g\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2134773389, :site 1}], :clock 7} \"h\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2138902582, :site 1}], :clock 8} \"i\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2142047365, :site 2}), :clock 9} \"j\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2145325706, :site 1}], :clock 10} \"k\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2146403937, :site 2}), :clock 11} \"l\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2146679479, :site 1}], :clock 12} \"m\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2146831807, :site 1}], :clock 13} \"n\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2147086330, :site 2}), :clock 14} \"o\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147434528, :site 1}], :clock 15} \"p\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2147438360, :site 2}), :clock 16} \"q\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147465165, :site 1}], :clock 17} \"v\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2147476235, :site 2}), :clock 18} \"w\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147479166, :site 1}], :clock 19} \"x\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147481375, :site 1}], :clock 20} \"y\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2147481802, :site 2}), :clock 21} \"z\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147482738, :site 1}], :clock 22} \"1\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2147483091, :site 2}), :clock 23} \"2\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147483186, :site 1}], :clock 24} \"3\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2147483564, :site 2}), :clock 25} \"4\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147483604, :site 1}], :clock 26} \"5\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2147483642, :site 2}), :clock 27} \"6\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147483643, :site 1}], :clock 28} \"7\",  #logoot.pid.PositionIdentifier{:positions (#logoot.pid.Position{:pos 2147483646, :site 2}), :clock 29} \"8\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147483646, :site 2} #logoot.pid.Position{:pos 1999385335, :site 1}], :clock 30} \"9\",  #logoot.pid.PositionIdentifier{:positions [#logoot.pid.Position{:pos 2147483647, :site 0}], :clock nil} \"<\"]")))
(def default-document (new-document))

; (def default-change-history [{:insert (as-> default-document $
;                                (rest $)
;                                (butlast $)
;                                (mapv vector (keys $) (vals $)))}])
(def default-change-history [])
(def document (atom default-document))
; (def change-history (atom []))
(def change-history (atom default-change-history))


(defrpc push-changes [changes] 
  {:rpc/query ["ok"]}
  (println "push-change/valid? document = " (valid? @document))
  (println "push-changes/changes = " changes)
  (swap! document #(first (m/merge-changes % (edn/map->changes changes))))
  (println "push-changes/document = " (display @document))
  (swap! change-history #(conj % (edn/map->changes changes))))

(defrpc pull-changes [etag]
  (let [good-etag (or etag 0)
        changes (drop good-etag @change-history)
        etag' (+ good-etag (count changes))]
    (when-not (empty? changes)
      (println "pull-changes/result  =" {:etag etag' :changes changes})
      (println "pull-changes/document = " @document))
    {:etag etag' :changes (map edn/changes->map changes)}))

(defrpc clear! 
  []
  (println "clear!")
  (reset! document default-document)
  (reset! change-history default-change-history)
  {:etag (count @change-history) :changes (map edn/changes->map @change-history)})

(defrpc get-state []
  {:random (rand-int 100)})

