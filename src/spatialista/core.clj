(ns spatialista.core
  (:use [compojure.core]
        [org.httpkit.server :only [run-server]]
        [ring.middleware.defaults]
        [ring.middleware.json]
        [ring.middleware.gzip]
        [ring.middleware.session.memory]
        [ring.middleware.session.cookie]
        [ring.util.response :only [response]]
        [spatialista.route]
        [spatialista.middlewares.cookies :only [wrap-spatialista-cookies]]
        [spatialista.middlewares.session :only [mem wrap-spatialista-session wrap-spatialista-flash]]
        [spatialista.utils.validation :only [wrap-spatialista-validation]]
        [spatialista.filter])
  (:require [clojure.tools.logging :as log]
            [compojure.route :as route]
            [ring.middleware.reload :as reload]
            [selmer.parser :as parser]))

(parser/cache-off!)
(parser/set-resource-path! (clojure.java.io/resource "spatialista/views"))

(def app
  (-> app-routes
      (wrap-json-params)
      (wrap-json-response)
      (wrap-defaults (dissoc site-defaults :session))
      (wrap-spatialista-validation)
      (wrap-spatialista-cookies)
      (wrap-spatialista-flash)
      (wrap-spatialista-session
       (update-in
        {:cookie-name "spatialista-session"}
        [:store] #(or % (memory-store mem))))
      (wrap-gzip)))

;; (def app
;;   (noir-middleware/app-handler [app-routes]
;;                                :session-options {:cookie-name "spatialista-session"}
;;                                :middleware [wrap-gzip]))

(defn -main [& args]
  (let [handler
        (reload/wrap-reload app)]
    (run-server handler {:port 3000})
    (println "Listen on 3000")))
