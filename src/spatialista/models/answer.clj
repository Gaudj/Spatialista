(ns spatialista.models.answer
  (:use [korma.core]
        [spatialista.models.db])
  (:require [clojure.string :as str]
            [spatialista.middlewares.session :as session]))

(defentity user)

(defentity answer
  (belongs-to user))

(defn get-answers-by-questionid [question-id]
  (select answer
          (with user)
          (fields :id :content :user.username :created_on :up_vote :down_vote)
          (where {:question_id question-id})
          (order :created_on)))

(defn insert-answer [content user-id question-id]
  (insert answer
          (values {:content content
                   :user_id user-id
                   :question_id question-id})))
