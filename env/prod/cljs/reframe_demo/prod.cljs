(ns reframe-demo.prod
  (:require
    [reframe-demo.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
