(ns spatialista.handlers.question
  (:use [ring.middleware.anti-forgery]
        [ring.util.response :only [redirect response]]
        [selmer.parser :only [render-file]]
        [selmer.filters]
        [spatialista.helper])
  (:require [clojure.string :as str]
            [spatialista.models.question :as question-model]
            [spatialista.models.tag :as tag-model]
            [spatialista.models.answer :as answer-model]
            [spatialista.middlewares.session :as session]))

(defn index [params]
  (let [page (params :page "1")]
    (render-file "questions/index.html"
                 {:tab "newest"
                  :questions (question-model/get-questions page)
                  :user (get-current-user)})))

(defn hottest [params]
  (let [page (params :page "1")]
    (render-file "questions/index.html"
                 {:tab "hottest"
                  :questions (question-model/get-hottest-questions page)})))

(defn unanswered [params]
  (let [page (params :page "1")]
    (render-file "questions/index.html"
                 {:tab "unanswered"
                  :questions (question-model/get-unanswered-questions page)})))

(defn new []
  (if (session/get :username)
    (render-file "questions/new.html" {:csrf-token *anti-forgery-token*})
    (redirect "/signin")))

(defn create [title tags content]
  (question-model/insert-question title tags content)
  (redirect "/"))

(defn show [id]
  (render-file "questions/show.html"
               {:question (question-model/get-question (read-string id))
                :answers (answer-model/get-answers-by-questionid (read-string id))
                :user (get-current-user)
                :csrf-token *anti-forgery-token*}))

(defn edit [id]
  (str "question.edit" id))

(defn update [id]
  (str "question.update" id))

(defn destroy [id]
  (str "question.destroy" id))

(defn answer [content question-id referer]
  (let [user-id (session/get! :uid)
        question-id (read-string question-id)
        callback-uri referer]
    (do
      (answer-model/insert-answer content user-id question-id)
      (redirect callback-uri))))
