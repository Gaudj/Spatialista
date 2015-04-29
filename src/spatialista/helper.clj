(ns spatialista.helper
  (:require [clojure.string :as str]
            [spatialista.middlewares.session :as session]))

(defn get-current-user []
  {:uid (session/get :uid)
   :username (session/get :username)})

(defn signin? []
  (if ((session/get :username))
    true
    false))

(defn admin? []
  ())
