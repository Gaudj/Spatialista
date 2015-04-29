(ns spatialista.models.blog
  (:use [korma.core]
        [spatialista.models.db])
  (:require [clojure.string :as str]))

(defentity blog)

(defn get-blogs [page]
  (select blog
          (order :created_on :DESC)
          (limit 26)
          (offset (* 26 (- (bigint page) 1)))))

(defn get-blog [id]
  (first
   (select blog
           (where {:id id}))))
