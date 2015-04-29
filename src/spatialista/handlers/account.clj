(ns spatialista.handlers.account
  (:use [ring.middleware.anti-forgery]
        [ring.util.response :only [redirect response]]
        [selmer.parser :only [render-file]])
  (:require [spatialista.utils.crypt :as crypt]
            [spatialista.models.user :as user-model]
            [spatialista.middlewares.session :as session]
            [spatialista.utils.validation :as validation]))

(defn new []
  (render-file "templates/accounts/new.html" {:csrf-token *anti-forgery-token*}))

(defn create [account password]
  (if (and (validation/has-value? account) (validation/has-value? password))
    (do
      (if (validation/is-email? account)
        (do
          (if-let [user (user-model/get-user-by-email account)]
            (if (crypt/compare password (user :password))
              (do
                (session/put! :uid (user :id))
                (session/put! :username (user :username))
                (redirect "/"))
              (redirect "/signin"))
            (redirect "/signin")))
        (if-let [user (user-model/get-user-by-username account)]
          (if (crypt/compare password (user :password))
            (do
              (session/put! :uid (user :id))
              (session/put! :username (user :username))
              (redirect "/"))
            (redirect "/signin"))
          (redirect "/signin"))))
    (redirect "/signin")))

(defn destroy []
  (session/remove! :uid)
  (session/remove! :username)
  (redirect "/"))

(defn reset []
  ())
