(ns hw-api.api
  (:require [cheshire.core :refer [generate-string]]
            [compojure.route :refer [files not-found]]
            [compojure.core :refer [defroutes GET POST DELETE ANY context]]
            [compojure.handler :refer [site]]
            [org.httpkit.server :refer :all]
            [hw-api.core :as core :refer [ingested-data]]
            [hw-api.ingest :as ingest :refer [parsed-map]]))

(defn json-resp
  "dissoc :sortable-dob from ppl maps, and render response map with resulting vector of maps encoded as json"
  [in-vec]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (-> (mapv #(dissoc % :sortable-dob) in-vec)
             (generate-string {:pretty true}))})

(defn by-gender-sort []
  (->> @core/ingested-data
       (sort-by :sex)))

(defn people-by-gender []
  (-> (by-gender-sort)
      json-resp))

(defn by-dob-sort []
  (->> @core/ingested-data
       (sort-by :sortable-dob)))

(defn people-by-dob []
  (-> (by-dob-sort)
      json-resp))

(defn by-last-name-first-name [x y]
  (compare [(:last-name x) (:first-name x)]
           [(:last-name y) (:first-name y)]))

(defn by-name-sort []
  (->> @core/ingested-data
       (sort by-last-name-first-name)))

(defn people-by-name []
  (-> (by-name-sort)
      json-resp))

(defn post-new-record [req-string]
  (let [parsed-record (parsed-map req-string)]
    (if-not (nil? parsed-record)
      (do
        (swap! ingested-data conj parsed-record)
        {:status 200
         :headers {"Content-Type" "application/json"}
         :body (generate-string (dissoc parsed-record :sortable-dob)
                                {:pretty false})}))))

(defroutes api-routes
  (GET "/records/gender" [] (people-by-gender))
  (GET "/records/birthdate" [] (people-by-dob))
  (GET "/records/name" [] (people-by-name))
  (POST "/records" req (post-new-record (-> req :body .bytes String.))))

(defn launch [& port]
  (run-server (site #'api-routes) {:port (or (first port) 3333)}))

