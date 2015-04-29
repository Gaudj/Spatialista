(ns spatialista.models.comment
  (:use [korma.core]
        [spatialista.models.db])
  (:require [clojure.string :as str]))

(defentity comment)
