(ns spatialista.models.db
  (:use [spatialista.config]
        [korma.db]))

(defdb db {:classname (:classname database)
           :subprotocol (:subprotocol database)
           :user (:user database)
           :password (:password database)
           :subname (:subname database)})
