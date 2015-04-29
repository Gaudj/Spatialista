(defproject spatialista "0.1.0-SNAPSHOT"
  :description "Spatialista"
  :url "http://spatialista.com"
  :license {:name "New BSD License"
            :url "https://github.com/Gaudj/spatialista/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-devel "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [ring/ring-json "0.3.1"]
                 [ring/ring-session-timeout "0.1.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [lobos "1.0.0-beta3"]
                 [korma "0.4.0"]
                 [postgresql/postgresql "9.3-1102.jdbc41"]
                 [http-kit "2.1.18"]
                 [compojure "1.3.1"]
                 [selmer "0.8.2"]
                 [clojurewerkz/scrypt "1.2.0"]
                 [com.taoensso/carmine "2.9.0"]
                 [clj-time "0.9.0"]
                 [org.clojars.mikejs/ring-gzip-middleware "0.1.0-SNAPSHOT"]]
  :ring {:handler spatialista.core/app}
  :main spatialista.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
