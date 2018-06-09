(ns hw-api.core
  (require [hw-api.ingest :as ingest]
           [clojure.pprint :refer [pprint]]))

(def ingested-data (atom []))

(defn print-ppl
  "dissoc :sortable-dob from ppl maps, and pprints the resulting vector of maps"
  [in-vec]
  (->> in-vec
       (mapv #(dissoc % :sortable-dob))
       pprint))

(defn init-data-atom
  "Initialize ingested-data atom with rows loaded by ingest/load-files"
  [in-path]
  ;TODO - handle exceptions
  (reset! ingested-data (ingest/load-files in-path)))


;; sorting and sort utility fns

(defn sorted-last-name-desc
  "Return contents of ingested-data, ordered by last name in descending order"
  []
  (into [] (sort-by :last-name (comp - compare) @ingested-data)))

(defn by-sex-last-name [x y]
  (compare [(:sex x) (:last-name x)]
           [(:sex y) (:last-name y)]))

(defn sorted-sex-last-name-asc
  "Returns contents of ingested-data, ordered by sex (asc) and last name (asc)"
  []
  (into [] (sort by-sex-last-name @ingested-data)))

(defn sorted-dob-asc
  "Returns contents of ingested-data, ordered by :date-of-birth (asc)"
  []
  (into [] (sort-by :sortable-dob @ingested-data)))


;; sorted lists of people as loaded from input files and added via API

(defn people-by-last-name-desc
  []
  (print-ppl (sorted-last-name-desc)))

(defn people-by-sex-last-name-asc
  []
  (print-ppl (sorted-sex-last-name-asc)))

(defn people-by-dob-asc
  []
  (print-ppl (sorted-dob-asc)))

