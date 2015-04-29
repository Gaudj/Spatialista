(ns spatialista.handlers.user
  (:use [ring.middleware.anti-forgery]
        [ring.util.response :only [redirect response]]
        [selmer.parser :only [render-file]])
  (:require [spatialista.utils.crypt :as crypt]
            [spatialista.models.user :as user-model]
            [spatialista.middlewares.session :as session]
            [spatialista.utils.validation :as validation]))

(defn index []
  (response (user-model/get-users)))

(defn new []
  (render-file "templates/users/new.html" {:csrf-token *anti-forgery-token*}))

(defn create [username email password]
  (if (and (validation/has-value? username) (validation/min-length? username 5) (validation/max-length? username 20) (validation/has-value? email) (validation/is-email? email) (validation/has-value? password) (validation/min-length? password 6))
    (do
      (if-let [user (user-model/insert-user username email (crypt/encrypt password))]
        (do
          (session/put! :uid (user :id))
          (session/put! :username (user :username))
          (redirect "/"))
        (redirect "/signup")))
    (redirect "/signup")))

(defn show [username]
  (response (user-model/get-user-by-username username)))

(defn edit [id]
  ())

(defn update [id]
  ())

(defn destroy [id]
  ())

(defn checkusername [username]
  (if-not (user-model/get-user-by-username username)
    "true"
    "false"))

(defn checkemail [email]
  (if-not (user-model/get-user-by-email email)
    "true"
    "false"))
