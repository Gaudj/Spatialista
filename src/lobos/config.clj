(ns lobos.config
  (:use [spatialista.config]
        [lobos.connectivity]))

(def db {:classname (:classname database)
         :subprotocol (:subprotocol database)
         :user (:user database)
         :password (:password database)
         :subname (:subname database)})
