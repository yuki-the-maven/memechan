(ns memechan.main
  (:require [ring.adapter.jetty :as ring-j]
            [memechan.core :refer :all]))

(ring-j/run-jetty handler {:port 3000})