(ns hw-api.ingest-test
  (:require [clojure.test :refer :all]
            [hw-api.ingest :refer :all]))

(deftest test-delimiter-select
  (testing "fn delimiter-select correctly IDs delimiter in use in line string."
    (is (= \| (delimiter-select "asdf|qwerty")))
    (is (= \, (delimiter-select "asdf, qwerty")))
    (is (= \space (delimiter-select "asdf qwerty")))
    (is (nil? (delimiter-select "asdf%qwerty")))))

(deftest test-parse-line
  (testing "fn parse-line correctly parses line-string with delimiter"
    (is (= ["Mayer" "John" "M" "Green" "10/16/1977"]
           (parse-line "Mayer | John | M | Green | 10/16/1977\n")))
    (is (= ["Bonamassa" "Joe" "M" "Blues" "05/08/1977"]
           (parse-line "Bonamassa, Joe, M, Blues, 05/08/1977\n")))
    (is (= ["Clark" "Gary" "M" "Blues" "02/15/1984"]
           (parse-line "Clark Gary M Blues 02/15/1984")))))

(deftest test-parsed-map
  (testing "fn parse-line correctly parses line-string with delimiter"
    (is (= {:last-name "Mayer", :first-name "John", :sex "M",
            :favorite-color "Green" :date-of-birth "10/16/1977"}
           (parsed-map "Mayer | John | M | Green | 10/16/1977\n")))
    (is (= {:last-name "Bonamassa", :first-name "Joe", :sex "M",
            :favorite-color "Blues" :date-of-birth "05/08/1977"}
           (parsed-map "Bonamassa, Joe, M, Blues, 05/08/1977\n")))
    (is (= {:last-name "Clark", :first-name "Gary", :sex "M",
            :favorite-color "Blues" :date-of-birth "02/15/1984"}
           (parsed-map "Clark Gary M Blues 02/15/1984\n")))))

(deftest test-list-data-files
  (testing "fn list-data-files returns list of objects"
    (let [fseq (list-data-files "./resources/data/")]
      (is (not (realized? fseq)))
      (is (every? #(= (class %) java.io.File) fseq)))))

(deftest test-load-files
  (testing "fn load-files returns vector of maps"
    (let [out-seq (load-files "./resources/data/")]
      (is (vector? out-seq))
      (is (every? map? out-seq))
      (is (every? (fn [x] (= (into [] (keys x))
                            [:last-name :first-name :sex
                             :favorite-color :date-of-birth]))
                  out-seq)))))

