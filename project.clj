(defproject memechan "0.1.0-SNAPSHOT"
  :description "be lame, use memes"
  :url "https://github.com/yuki-the-maven/memechan"
  :license {:name "MIT"
            :url "http://www.opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.apache.tika/tika-core "1.13"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]]
  :main memechan.main)
