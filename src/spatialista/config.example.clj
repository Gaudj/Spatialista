(ns spatialista.config)

;; database
(def database {:classname "org.postgresql.Driver"
               :subprotocol "postgresql"
               :user ""
               :password ""
               :subname "//localhost:5432/test"})

;; redis
(def redis {:host ""
            :port ""
            :db ""})

;; email
(def mailer {:server ""
             :port ""
             :username ""
             :password ""})
