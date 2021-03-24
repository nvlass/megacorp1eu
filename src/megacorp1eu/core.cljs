(ns megacorp1eu.core
  #_(:require [clojure.browser.repl :as repl]))

;; (defonce conn
;;   (repl/connect "http://localhost:9000/repl"))

(enable-console-print!)

(println "The MegaCorp knows.")

(def screen-dim 600)
(def screen-dim-2 300)
(def box-edge 4)

(def ctx
  (.getContext (.getElementById js/document "the-megacorp1-knows")
               "2d"))

(def pts {;; :p0 {:x 0 :y 0} ;; not yet
          :p0 {:x 0 :y -70}
          :p1 {:x -70 :y 53.62}
          :p2 {:x 70 :y 53.62}})

(def lns [[:p0 :p1] [:p1 :p2] [:p0 :p2]])

(def color-map
  ["#11FF11"
   "#00AA00"
   "#008800"
   "#005500"
   "#003300"
   "#001100"
   "#000000"])

;;; rewrite as it is somewhat ugly
;;; FIXME: missed a sign somewhere?
(defn segmentize-line
  [[{x0 :x y0 :y}
    {x1 :x y1 :y}]]
  (let [seg-count   (+ 30 (rand-int 30))
        ;; accuracy fails -- but nice effect
        lam         (/ (- y1 y0) (- x1 x0))
        ;; divide [0, 1] into seg-count parts with random length
        segs-u      (->> (repeatedly rand)
                         (take seg-count)
                         (reductions +))
        max-seg-len (apply max segs-u)
        segs        (map (fn [x] (/ x max-seg-len)) segs-u)
        jx         (* (- (rand) 0.5) 3)
        jy         (* (- (rand) 0.5) 3)
        ;; do the `t` thing
        pts         (map (fn [t]
                           (let [x (+ x0 (* (- x1 x0) t))
                                 y (+ y0 (* lam x))]
                             {:x (+ x jx)
                              :y (+ y jy)}))
                         segs)]
    (map (fn [p1 p2] [p1 p2]) pts (drop 1 pts))))


;; a pattern emerges?
(defn transl-coords [{:keys [x y] :as p}]
  (assoc p
         :plot-x (+ (* x box-edge) screen-dim-2)
         :plot-y (+ (* y box-edge) screen-dim-2)))

(defn plot-line-segs
  [lsegs]
  (doseq [[p1 p2] lsegs]
    (.beginPath ctx)
    (set! (.-strokeStyle ctx) (rand-nth color-map))
    (let [pp1 (transl-coords p1)
          pp2 (transl-coords p2)]
      (.moveTo ctx (:plot-x pp1) (:plot-y pp1))
      (.lineTo ctx (:plot-x pp2) (:plot-y pp2))
      (.stroke ctx))))
  

(defn plot-seg-line [ln]
  (->> (map pts ln)
       (segmentize-line)
       (plot-line-segs)))

(defn seg-tri []
  (doseq [ln lns]
    (plot-seg-line ln)))

(defn tri []
  (.beginPath ctx)
  (set! (.-strokeStyle ctx) "#11FF11")
  (doseq [[p1 p2] lns]
    (let [pp1 (->> p1 (get pts) transl-coords)
          pp2 (->> p2 (get pts) transl-coords)]
      (.moveTo ctx (:plot-x pp1) (:plot-y pp1))
      (.lineTo ctx (:plot-x pp2) (:plot-y pp2))))
  (.stroke ctx))


(def interv
  (.setInterval
   js/window (fn []
               (if (< (rand) 0.8) (seg-tri)
                   (tri)))
   30))
