(ns memechan.core)

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
  [dir]
  (filter is-image? (file-seq dir)))


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
