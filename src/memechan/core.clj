(ns memechan.core
  (:require [ring.middleware.file :as ring-file])
  (:import (java.net URLEncoder)))

;;; detection
(defn is-image?
  "Determines if the file is an image with mimetype detection"
  [^java.io.File file]
  (and
    (.isFile file)
    (.startsWith
      (.detect (org.apache.tika.Tika.) file)
      "image/")))

(defn seq-images
  [^java.io.File dir]
  (cond
    (not (.exists dir)) (throw (IllegalArgumentException. "dir must exist"))
    (not (.isDirectory dir)) (throw (IllegalArgumentException. "dir must be a directory"))
    :default (filter is-image? (file-seq dir))))


;;;; hashing

(defn bytes-to-hex [bytes]
  (apply str (map (partial format "%02x") bytes)))

(defn- consume-stream [^java.security.DigestInputStream stream]
  (let [buffer (byte-array 1024)]
    (while (not (= -1 (.read stream (byte-array 1024)))))
    stream))

(defn- generic-hashsum [algorithm ^java.io.File file]
  (bytes-to-hex
    (.digest (.getMessageDigest
      (consume-stream (java.security.DigestInputStream.
        (java.io.FileInputStream. file)
        (java.security.MessageDigest/getInstance algorithm)))))))

(def sha1sum 
  (partial generic-hashsum "sha1"))

(def md5sum
  (partial generic-hashsum "md5"))

(defn make-hash
  "given a file returns an opaque structure representing its hash"
  [file]
  [(md5sum file) (sha1sum file)])

;;;; indexing

(defn make-index
  "make an index (opaque struct) from a seq of files"
  [files]
  (reduce merge
    (map (fn [file] {(.getName file) (make-hash file)})
      files)))

(defn fetch-hash
  "fetch the hash for a file from an index"
  [index ^java.io.File file]
  (get index (.getName file)))


;;;; urlencoding

(defn url-encode [string]
  (.replace
    (URLEncoder/encode string "UTF-8")
    "+"
    "%20"))

;;;; web

(defn layout[^String content]
  (apply str
    "<!DOCTYPE html>"
    "<html>"
      "<head>"
        "<style>"
".content img {"
"  max-height:200px;"
"  max-width:300px;"
"  margin: 3rem;"
"}"
".content {"
"    display: flex;"
"    flex-flow: row wrap;"
"    justify-content: space-around;"
"}"
        "</style>"
      "</head>"
      "<body>"
        "<h1>memechan</h1>"
        "<div class=\"content\">"
          content
        "</div>"
      "</body>"
    "</html>"
    ))

(defn as-img-tag[^java.io.File image]
  (str "<img src=\"" (url-encode (.getName image)) "\" alt=\"" (.getName image) "\">"))

(defn main-page[layouter image-renderer images]
  (layout
    (apply str
      (map image-renderer images))))

(def default-main-page
  (partial main-page layout as-img-tag))

(defn make-main-page-handler [pics-dir]
  (ring-file/wrap-file 
    (fn [request]
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (default-main-page (seq-images pics-dir))})
    pics-dir))