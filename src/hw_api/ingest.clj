(ns hw-api.ingest
  (:require [clojure.string :as s]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn delimiter-select
  "Identifies and returns the delimiter in use in the line passed as line-string."
  [line-string]
  (cond
    (s/includes? line-string "|") \|
    (s/includes? line-string ",") \,
    (s/includes? line-string " ") \space
    :else nil))

(defn parse-line
  "Parse line-string as received from delimited text to vector"
  [line-string]
  (let [separator (delimiter-select line-string)]
    (if-not (nil? separator)
      (let [parsed-vec  (-> line-string
                            (csv/read-csv :separator separator)
                            first)]
        (mapv s/trim parsed-vec))
      (throw (Exception. "Unknown data format.")))))

(defn parsed-map
  "Accepts a string containing a line of delimited data, and zips into a map"
  [line-string]
  (->> line-string
       parse-line
       (zipmap [:last-name :first-name :sex :favorite-color :date-of-birth])))

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
