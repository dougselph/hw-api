(ns hw-api.core-test
  (require [clojure.test :refer :all]
           [hw-api.core :refer :all]
           [hw-api.ingest :as ingest]))

(defn core-test-fixture
  "Initializes data atom for tests in core ns"
  [f]
  (reset! ingested-data [])
  (init-data-atom "./resources/data/")
  (f))

(use-fixtures :once core-test-fixture)

(deftest test-core-data-atom
  (testing "fn init-data-atom loads data from input files correctly"
    (is (= @ingested-data (ingest/load-files "./resources/data/")))))

(deftest test-sorted-last-name-desc
  (testing "fn sorted-last-name-desc returns contents of atom ingested-data,
ordered by :last-name descending"
    (is (= (mapv :last-name (sorted-last-name-desc))
           (sort (comp - compare) (mapv :last-name @ingested-data))))
    (is (every? (fn [x] (<= x 0))
                (->> (sorted-last-name-desc)
                     (mapv :last-name)
                     (partition 2 1)
                     (map (fn [[x y]] ((comp - compare) x y))))))
    (is (= (count (sorted-last-name-desc)) (count @ingested-data)))
    ;; this is 6 because :sortable_dob is present in the record at this stage
    (is (every? (fn [x] (= (count x) 6)) (sorted-last-name-desc)))))

(deftest test-sorted-sex-last-name-asc
  (testing "fn sorted-sex-last-name-asc returns contents of atom ingested-data,
ordered by :sex, :last-name ascending"
    (is (= (mapv (fn [x]  [(:sex x) (:last-name x)]) (sorted-sex-last-name-asc))
           (sort compare (map (fn [x]  [(:sex x) (:last-name x)]) @ingested-data))))))

