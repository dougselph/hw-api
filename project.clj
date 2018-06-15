(defproject hw_api "0.1.0-SNAPSHOT"
  :description "Parse varied inputs and publish to console or REST API"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [clj-time "0.14.4"]
                 [compojure "1.6.1"]
                 [http-kit "2.2.0"]
                 [cheshire "5.8.0"]]
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]]}})

;;:plugins [[lein-ring "0.9.7"]]
;;:ring {:handler hw-api.api/app}

;;[ring/ring-core "1.6.3"]
;;[ring/ring-jetty-adapter "1.6.3"]
;;[ring/ring-defaults "0.2.1"]

