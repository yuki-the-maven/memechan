(ns memechan.main
  (:require [ring.adapter.jetty :as ring-j]
            [memechan.core :refer :all]))

;;;; stuff that will need to become configuration
(def pics-dir
  (java.io.File. (java.lang.System/getProperty "user.home") "Pictures/weapons/"))

(ring-j/run-jetty (make-main-page-handler pics-dir) {:port 3000})