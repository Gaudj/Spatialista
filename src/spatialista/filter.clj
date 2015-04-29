(ns spatialista.filter
  (:use [selmer.filters])
  (:require [clojure.string :as str]))

(add-filter! :split-tag
             (fn [s]
               (str/split s #",")))

(add-filter! :pagination
             (fn [page uri]
               ()))
