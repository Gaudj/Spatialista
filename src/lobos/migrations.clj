(ns lobos.migrations
  (:refer-clojure :exclude [create alter drop
                            bigint boolean char double float time])
  (:use [lobos.migration :only [defmigration]]
        [lobos.core]
        [lobos.schema]
        [lobos.config]
        [lobos.helpers]))

(defmigration add-user-table
  (up [] (create (tbl :user
                      (text :username :unique)
                      (text :username_lower :unique)
                      (text :email :unique)
                      (text :email_lower :unique)
                      (text :password)
                      (text :avatar)
                      (integer :role)
                      (text :location)
                      (text :website)
                      (text :company))))
  (down [] (drop (table :user))))

(defmigration add-username-index
  (up [] (create (index :user [:username_lower])))
  (down [] (index :user [:username_lower])))

(defmigration add-email-index
  (up [] (create (index :user [:email_lower])))
  (down [] (index :user [:email_lower])))

(defmigration add-question-table
  (up [] (create (tbl :question
                      (text :title)
                      (text :content)
                      (text :tags)
                      (integer :status)
                      (integer :views)
                      (integer :answer_count)
                      (integer :user_id)
                      (integer :up_vote)
                      (integer :down_vote))))
  (down [] (drop (table :question))))

(defmigration add-answer-table
  (up [] (create (tbl :answer
                      (text :content)
                      (integer :user_id)
                      (integer :question_id)
                      (integer :comment_count)
                      (integer :up_vote)
                      (integer :down_vote))))
  (down [] (drop (table :answer))))

(defmigration add-blog-table
  (up [] (create (tbl :blog
                      (text :title)
                      (text :content)
                      (text :tags)
                      (integer :status)
                      (integer :views)
                      (integer :comment_count)
                      (integer :user_id))))
  (down [] (drop (table :blog))))

(defmigration add-atlas-table
  (up [] (create (tbl :atlas
                      (text :title)
                      (text :content)
                      (integer :status)
                      (integer :views)
                      (integer :comment_count)
                      (refer-to :user)
                      (integer :up_vote)
                      (integer :down_vote))))
  (down [] (drop (table :atlas))))

(defmigration add-comment-table
  (up [] (create (tbl :comment
                      (text :content)
                      (integer :user_id)
                      (integer :item_id)
                      (integer :parent_id)
                      (text :type)
                      (integer :up_vote)
                      (integer :down_vote))))
  (down [] (drop (table :comment))))

(defmigration add-tag-table
  (up [] (create (tbl :tag
                      (text :name :unique)
                      (text :description)
                      (integer :question_count)
                      (integer :blog_count)
                      (integer :atlas_count))))
  (down [] (drop (table :tag))))

(defmigration add-tagname-index
  (up [] (create (index :tag [:name])))
  (down [] (index :tag [:name])))

(defmigration add-tag-map-table
  (up [] (create (table :tag_map
                        (integer :tag_id)
                        (integer :item_id)
                        (text :type))))
  (down [] (drop (table :tag_map))))

(defmigration add-favorite-table
  (up [] (create (tbl :favorite
                      (text :content)
                      (integer :user_id)
                      (integer :item_id)
                      (text :type))))
  (down [] (drop (table :favorite))))

(defmigration add-vote-table
  (up [] (create (tbl :vote
                      (integer :status)
                      (integer :user_id)
                      (integer :item_id)
                      (text :type))))
  (down [] (drop (table :vote))))
