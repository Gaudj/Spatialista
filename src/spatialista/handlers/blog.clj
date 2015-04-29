(ns spatialista.handlers.blog
  (:use [ring.middleware.anti-forgery]
        [ring.util.response :only [redirect response]]
        [selmer.parser :only [render-file]])
  (:require [spatialista.models.blog :as blog-model]))

(defn index [params]
  (let [tab (params :tab)
        page (params :page 1)]
    (case tab
      "newest" (render-file "templates/blogs/index.html"
                            {:tab "newest"
                             :questions (blog-model/get-blogs page)})
      "hottest" (render-file "templates/blogs/index.html"
                             {:tab "hottest"
                              :questions (blog-model/get-blogs page)})
      "unanswered" (render-file "templates/blogs/index.html"
                                {:tab "unanswered"
                                 :questions (blog-model/get-blogs page)})
      "<h1>404! Page not found</h1>")))

(defn new []
  ())

(defn create []
  ())

(defn show [id]
  ())

(defn edit [id]
  ())

(defn update [id]
  ())

(defn destroy [id]
  ())
