(ns spatialista.route
  (:use [compojure.core]
        [ring.middleware.anti-forgery]
        [ring.middleware.params]
        [ring.util.response :only [redirect response]]
        [selmer.parser :only [render-file]])
  (:require [compojure.route :as route]
            [spatialista.handlers.question :as question]
            [spatialista.handlers.blog :as blog]
            [spatialista.handlers.user :as user]
            [spatialista.handlers.account :as account]))

(defroutes app-routes
  (GET "/" {params :params}
       (question/index params))

  (context "/questions" []
           (GET "/" [] (redirect "/"))
           ;; (GET "/newest" [] (redirect "/"))
           (GET "/hottest" {params :params} (question/hottest params))
           (GET "/unanswered" {params :params} (question/unanswered params))
           (GET "/new" [] (question/new))
           (POST "/" [title tags content] (question/create title tags content))
           (POST "/answer" [content question-id :as {{referer "referer"} :headers}] (question/answer content question-id referer))
           (context "/:id{[0-9]+}" [id]
                    (GET "/" [] (question/show id))
                    (GET "/edit" [] (question/edit id))
                    (PUT "/" [] (question/update id))
                    (DELETE "/" [] (question/destroy id))))

  (context "/blogs" []
           (GET "/" {params :params} (blog/index params))
           ;; (GET "/newest" [] (redirect "/blogs"))
           (GET "/hottest" {params :params} (question/index params))
           (GET "/unanswered" {params :params} (question/index params))
           (GET "/new" [] (blog/new))
           (POST "/" [] (blog/create))
           (context "/:id{[0-9]+}" [id]
                    (GET "/" [] (blog/show id))
                    (GET "/edit" [] (blog/edit id))
                    (PUT "/" [] (blog/update id))
                    (DELETE "/" [] (blog/destroy id))))

  ;; (context "/atlas" []
  ;;          (GET "/" {params :params} (atlas/index params))
  ;;          (GET "/newest" [] (redirect "/atlas"))
  ;;          (GET "/hottest" {params :params} (atlas/index params))
  ;;          (GET "/new" [] (atlas/new))
  ;;          (POST "/" [] (atlas/create))
  ;;          (context "/:id{[0-9]+}" [id]
  ;;                   (GET "/" [] (atlas/show id))
  ;;                   (GET "/edit" [] (atlas/edit id))
  ;;                   (PUT "/" [] (atlas/update id))
  ;;                   (DELETE "/" [] (atlas/destroy id))))

  (context "/tags" []
           (GET "/" [] ()))

  (context "/users" []
           (GET "/" [] (user/index))
           (GET "/new" [] (redirect "/signup"))
           (GET "/checkusername" [username] (user/checkusername username))
           (GET "/checkemail" [email] (user/checkemail email))
           (context "/:username" [username]
                    (GET "/" [] (user/show username)))
           (context "/:id{[0-9]+}" [id]
                    ;; (GET "/" [] (user/show id))
                    (GET "/edit" [] (user/edit id))
                    (PUT "/" [] (user/update id))
                    (DELETE "/" [] (user/destroy id))))

  (GET "/signup" [] (user/new))
  (POST "/signup" [username email password] (user/create username email password))
  (GET "/signin" [] (account/new))
  (POST "/signin" [account password] (account/create account password))
  (GET "/signout" [] (account/destroy))

  (context "/admin" []
           (GET "/" [] ()))

  (route/resources "/")
  (route/not-found "<h1>404! Page not found</h1>"))
