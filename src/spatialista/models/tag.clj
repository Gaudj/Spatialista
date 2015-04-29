(ns spatialista.models.tag
  (:use [korma.core]
        [spatialista.models.db])
  (:require [clojure.string :as str]))

(defentity tag)

(defn get-tagid-by-name [name]
  (select tag
          (fields :id)
          (where {:name [in (str/split name #",")]})))

(defn get-tag [id]
  (first
   (select tag
           (where {:id id}))))
