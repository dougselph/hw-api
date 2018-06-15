(ns hw-api.api-test
  (:require [clojure.test :refer :all]
            [hw-api.api :refer :all]
            [cheshire.core :refer [parse-string]]))

(deftest test-json-resp
  (testing "fn json-resp dissoc-es :sortable-dob from each map and embeds JSON string as value of :body key in response map"
    (let [result (json-resp @hw-api.core/ingested-data)]
      (is (map? result))
      (is (= (:status result) 200))
      (is (= (:headers result) {"Content-Type" "application/json"}))
      (is (= (-> result :body parse-string count)
             (count @hw-api.core/ingested-data)))
      (is (= (-> result :body parse-string first keys)
             '("last-name" "first-name" "sex" "favorite-color" "date-of-birth"))))))

(deftest test-by-gender-sort
  (testing "fn by-gender-sort returns a sequence of maps sorted properly by :sex"
    (let [result (by-gender-sort)]
      (is (seq? result))
      (is (every? (fn [x] (map? x)) result))
      (is (every? (fn [x] (<= x 0))
                  (->> result
                       (mapv :sex)
                       (partition 2 1)
                       (map (fn [[x y]] (compare x y))))))
      (is (every? (fn [x] (not (nil? (:sortable-dob x)))) result)))))

(deftest test-by-dob-sort
  (testing "fn by-dob-sort returns a sequence of maps sorted properly by :sortable-dob"
    (let [result (by-dob-sort)]
      (is (seq? result))
      (is (every? (fn [x] (map? x)) result))
      (is (apply <= (map :sortable-dob result)))
      (is (every? (fn [x] (not (nil? (:sortable-dob x)))) result)))))

(deftest test-by-name-sort
  (testing "fn by-name-sort returns a sequence of maps sorted properly by :last-name, :first-name"
    (let [result (by-name-sort)]
      (is (seq? result))
      (is (every? (fn [x] (map? x)) result))
      (is (every? (fn [x] (<= x 0))
                  (->> result
                       (mapv (fn [x] [(:last-name x) (:first-name x)]))
                       (partition 2 1)
                       (map (fn [[x y]] (compare x y))))))
      (is (every? (fn [x] (not (nil? (:sortable-dob x)))) result)))))

(deftest test-people-by-gender
  (testing "fn people-by-gender returns a response map and embeds JSON string as value of :body key"
    (let [result (people-by-gender)]
      (is (map? result))
      (is (= (:status result) 200))
      (is (= (:headers result) {"Content-Type" "application/json"}))
      (is (= (-> result :body parse-string count)
             (count @hw-api.core/ingested-data)))
      (is (every? (fn [x] (nil? (x "sortable-dob"))) (-> result :body parse-string)))
      (is (= (-> result :body parse-string first keys)
             '("last-name" "first-name" "sex" "favorite-color" "date-of-birth"))))))

(deftest test-people-by-dob
  (testing "fn people-by-dob returns a response map and embeds JSON string as value of :body key"
    (let [result (people-by-dob)]
      (is (map? result))
      (is (= (:status result) 200))
      (is (= (:headers result) {"Content-Type" "application/json"}))
      (is (= (-> result :body parse-string count)
             (count @hw-api.core/ingested-data)))
      (is (every? (fn [x] (nil? (x "sortable-dob"))) (-> result :body parse-string)))
      (is (= (-> result :body parse-string first keys)
             '("last-name" "first-name" "sex" "favorite-color" "date-of-birth"))))))

(deftest test-people-by-name
  (testing "fn people-by-name returns a response map and embeds JSON string as value of :body key"
    (let [result (people-by-dob)]
      (is (map? result))
      (is (= (:status result) 200))
      (is (= (:headers result) {"Content-Type" "application/json"}))
      (is (= (-> result :body parse-string count)
             (count @hw-api.core/ingested-data)))
      (is (every? (fn [x] (nil? (x "sortable-dob"))) (-> result :body parse-string)))
      (is (= (-> result :body parse-string first keys)
             '("last-name" "first-name" "sex" "favorite-color" "date-of-birth"))))))

(deftest test-post-new-record
  (testing "fn post-new-record receives a valid delimited string containting a new person record, and conj-es it onto the atom @ingested-data"
    (let [count-1 (count @hw-api.core/ingested-data)
          result-1 (post-new-record "Doo | Scooby | M | Green | 01/15/1974")
          last-1 (last @hw-api.core/ingested-data)
          count-2 (count @hw-api.core/ingested-data)
          result-2 (post-new-record "Albert, Fat, M, Red, 10/5/1971")
          last-2 (last @hw-api.core/ingested-data)
          count-3 (count @hw-api.core/ingested-data)
          result-3 (post-new-record "Racer Speed M Blue 04/13/1973")
          last-3 (last @hw-api.core/ingested-data)
          count-4 (count @hw-api.core/ingested-data)
          result-4 (post-new-record "Ranger$Power$F$Pink$08/27/1984")
          last-4 (last @hw-api.core/ingested-data)
          count-5 (count @hw-api.core/ingested-data)]
      (is (< count-1 count-2 count-3 count-4))
      (is (every? (fn [x] (map? x)) [result-1 result-2 result-3]))
      (is (nil? result-4))
      (is (= count-4 count-5))
      (is (= (inc count-1) count-2))
      (is (= (inc count-2) count-3))
      (is (= (inc count-3) count-4))
      (is (= 0 (compare last-3 last-4)))
      (is (= '("Doo" "Scooby" "M" "Green" "1/15/1974")
             (vals (dissoc last-1 :sortable-dob))))
      (is (= '("Albert" "Fat" "M" "Red" "10/5/1971")
             (vals (dissoc last-2 :sortable-dob))))
      (is (= '("Racer" "Speed" "M" "Blue" "4/13/1973")
             (vals (dissoc last-3 :sortable-dob))))
      (is (not (= "Ranger" (:last-name last-4))))
      (is (every? (fn [x] (not (nil? x))) [result-1 result-2 result-3]))
      (let [parsed-1 (-> result-1 :body parse-string)]
        (is (map? parsed-1))
        (is (= (keys parsed-1)
               '("last-name" "first-name" "sex" "favorite-color" "date-of-birth")))))))
