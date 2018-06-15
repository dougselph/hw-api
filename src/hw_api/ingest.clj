(ns hw-api.ingest
  (:require [clojure.string :as s]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [clj-time.core :as t]))

(def map-keywords [:last-name :first-name :sex :favorite-color :date-of-birth])

(defn delimiter-select
  "Identifies and returns the delimiter in use in the line passed as line-string."
  [line-string]
  (cond
    (s/includes? line-string "|") \|
    (s/includes? line-string ",") \,
    (s/includes? line-string " ") \space
    :else (throw (Exception. "- Unrecognized data format."))))

(defn parse-line
  "Parse line-string as received from delimited text to vector"
  [line-string]
  (let [separator (delimiter-select line-string)
        parsed-vec  (-> line-string
                        (csv/read-csv :separator separator)
                        first)]
    (mapv s/trim parsed-vec)))

(defn sortable-dob
  "Adds numeric datestamp value to in-map for sorting, and formats :date-of-birth
  as M/D/YYYY"
  [in-map]
  (let [in-format (f/formatter (t/default-time-zone)
                               "M/d/YYYY" "MM/dd/YYYY" "YYYY/MM/dd"
                               "YYYY-MM-dd" "MM-dd-YYYY" "M-d-YYYY")
        in-dob (:date-of-birth in-map)
        parsed-date (f/parse in-format in-dob)
        long-date (c/to-long parsed-date)
        formatted-date (f/unparse in-format parsed-date)]
    (assoc in-map :sortable-dob long-date :date-of-birth formatted-date)))

(defn parsed-map
  "Accepts a string containing a line of delimited data, and zips into a map"
  [line-string]
  (try
    (->> line-string
         parse-line
         (zipmap map-keywords)
         (sortable-dob))
    (catch Exception e
      (str "Exception: " (.getMessage e))
      nil)))

(defn list-data-files
  "Returns lazy seq of files contained in the directory at in-path."
  [in-path]
  (->> in-path
       io/file
       file-seq
       (filter #(.isFile %))))

(defn load-files
  "Returns vactor of maps populated by parsing files contained in directory at in-path."
  [in-path]
  (->> in-path
       list-data-files
       (map #(-> % io/reader line-seq rest))
       flatten
       (mapv parsed-map)))
