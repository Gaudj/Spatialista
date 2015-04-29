(ns spatialista.models.question
  (:use [korma.core]
        [spatialista.models.db])
  (:require [clojure.string :as str]
            [clj-time.core :as time]
            [clj-time.coerce :as tc]))

(def pagesize 26)

(defentity user)

(defentity question
  (belongs-to user))

(defn get-questions [page]
  (select question
          (with user)
          (fields :id :title :tags :user.username :user.avatar)
          (order :created_on :DESC)
          (limit 26)
          (offset (* 26 (- (read-string page) 1)))))

(defn get-hottest-questions [page]
  (select question
          (order :created_on :DESC)
          (limit pagesize)
          (offset (* pagesize (- (read-string page) 1)))))

(defn get-unanswered-questions [page]
  (select question
          (order :created_on :DESC)
          (limit 26)
          (offset (* 26 (- (read-string page) 1)))))

(defn get-question [id]
  (first
   (select question
          (with user)
          (fields :id :title :content :tags :user.username :user.avatar :answer_count :created_on :updated_on)
           (where {:id id}))))

(defn insert-question [title tags content]
  (insert question
          (values {:title title
                   :tags tags
                   :content content
                   :updated_on (tc/to-sql-time (time/now))})))
