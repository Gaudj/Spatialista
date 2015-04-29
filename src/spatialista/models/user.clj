(ns spatialista.models.user
  (:use [korma.core]
        [spatialista.models.db])
  (:require [clojure.string :as str]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]))

(defentity question)

(defentity user
  (has-many question))

(defn get-users []
  (select user))

(defn get-user-by-id [id]
  (first
   (select user
           (where {:id id}))))

(defn get-user-by-username [username]
  (first
   (select user
           (where {:username_lower (str/lower-case username)}))))

(defn get-user-by-email [email]
  (first
   (select user
           (where {:email_lower (str/lower-case email)}))))

(defn insert-user [username email password]
  (insert user
          (values {:username username
                   :username_lower (str/lower-case username)
                   :email email
                   :email_lower (str/lower-case email)
                   :password password
                   :role 1
                   :updated_on (tc/to-sql-time (time/now))})))

(defn update-user []
  (update user
          (set-fields {})
          (where {})))
