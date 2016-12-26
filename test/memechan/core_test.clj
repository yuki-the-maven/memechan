(ns memechan.core-test
  (:require [clojure.test :refer :all]
            [memechan.core :refer :all]))

(def shits_on_fire_yo
	(java.io.File. "test/test_files/shits_on_fire_yo.jpg"))
(def not_an_image
	(java.io.File. "test/test_files/not_an_image.png"))
(def test_files
	(java.io.File. "test/test_files/"))
(def files-in-testdir
	(filter (fn [^java.io.File file] (.isFile file)) (file-seq test_files)))

(deftest is-image?-t
  (testing "yields true for shits_on_fire_yo.jpg"
    (is (is-image? shits_on_fire_yo)))
  (testing "yields false for not_an_image.png"
    (is (not (is-image? not_an_image)))))

(deftest seq-images-t
	(testing "yields only shits_on_fire_yo when called for test_images dir"
		(is (= 
			    (list shits_on_fire_yo)
			    (seq-images test_files)))))

; hashing

(deftest sha1sum-test
	(testing "expected sha1 for shits_on_fire_yo"
		(is (= "f1310d674ac9e78600d717742c635708a22eab65" (sha1sum shits_on_fire_yo))))
	(testing "expected sha1 for not_an_image"
		(is (= "554a35676ba81cf63d93acea0139bba04a812ca4" (sha1sum not_an_image)))))

(deftest md5sum-test
	(testing "expected md5 for shits_on_fire_yo"
		(is (= "824c93d659485745393a0ac7ff744c26" (md5sum shits_on_fire_yo))))
	(testing "expected sha1 for not_an_image"
		(is (= "3c59afbefc127140d80b00a9af675f25" (md5sum not_an_image)))))

(deftest hashes
	(testing "getting hash multiple time for same file is consistent (as long as file doesn't change on disk ofc)"
		(is (= (make-hash shits_on_fire_yo) (make-hash shits_on_fire_yo))))
	(testing "hashes of different files are different (unless the content is the same ofc)"
		(is (not (= (make-hash not_an_image) (make-hash shits_on_fire_yo))))))

; indexing

(deftest indexing
	(testing "index for test_images contains shits_on_fire_yo's hash"
		(is (not (nil? (fetch-hash (make-index files-in-testdir) shits_on_fire_yo))))))