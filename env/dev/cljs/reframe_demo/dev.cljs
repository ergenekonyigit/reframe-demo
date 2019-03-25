(ns ^:figwheel-no-load reframe-demo.dev
  (:require
    [reframe-demo.core :as core]
    [devtools.core :as devtools]))


(enable-console-print!)

(devtools/install!)

(core/init!)
