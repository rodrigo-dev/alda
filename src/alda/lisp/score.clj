(ns alda.lisp.score
  (:require [alda.lisp.score.part]))
(in-ns 'alda.lisp)

(log/debug "Loading alda.lisp.score...")

(defn init-score
  []
  (letfn [(init [var val] (alter-var-root var (constantly val)))]
    (init #'*events* {:start {:offset (AbsoluteOffset. 0), :events []}})
    (init #'*global-attributes* {})
    (init #'*instruments* {})
    (init #'*current-instruments* #{})
    (init #'*nicknames* {})))

(defn event-set
  "Takes *events* in its typical form (organized by markers with relative
   offsets) and transforms it into a single set of events with absolute
   offsets."
  [events-map]
  (into #{}
    (mapcat (fn [[_ {:keys [offset events]}]]
              (for [event events]
                (update-in event [:offset] absolute-offset)))
            events-map)))

(defmacro score
  "Initializes a new score, evaluates body, and returns the map containing the
   set of events resulting from evaluating the score, and information about the
   instrument instances, including their states at the end of the score."
  [& body]
  `(do
     (init-score)
     ~@body
     {:events (event-set *events*)
      :instruments *instruments*}))