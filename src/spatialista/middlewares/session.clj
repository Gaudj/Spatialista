(ns spatialista.middlewares.session
  "Stateful session handling functions. Uses a memory-store by
  default, but can use a custom store by supplying a :session-store
  option to server/start."
  (:refer-clojure :exclude [get get-in remove swap!])
  (:use ring.middleware.session
        ring.middleware.session.memory
        ring.middleware.flash
        [ring.middleware.session-timeout
         :only [wrap-idle-session-timeout]]))

;; ## Session

(declare ^:dynamic *spatialista-session*)

(defonce mem (atom {}))

(defn- current-time []
  (quot (System/currentTimeMillis) 1000))

(defn- expired? [[id session]]
  (pos? (- (:ring.middleware.session-timeout/idle-timeout session) (current-time))))

(defn clear-expired-sessions []
  (clojure.core/swap! mem #(->> % (filter expired?) (into {}))))

(defn put!
  "Associates the key with the given value in the session"
  [k v]
  (clojure.core/swap! *spatialista-session* assoc k v))

(defn get
  "Get the key's value from the session, returns nil if it doesn't exist."
  ([k] (get k nil))
  ([k default]
    (clojure.core/get @*spatialista-session* k default)))

(defn get-in
  "Gets the value at the path specified by the vector ks from the session,
  returns nil if it doesn't exist."
  ([ks] (get-in ks nil))
  ([ks default]
    (clojure.core/get-in @*spatialista-session* ks default)))

(defn swap!
  "Replace the current session's value with the result of executing f with
  the current value and args."
  [f & args]
  (apply clojure.core/swap! *spatialista-session* f args))

(defn clear!
  "Remove all data from the session and start over cleanly."
  []
  (reset! *spatialista-session* {}))

(defn remove!
  "Remove a key from the session"
  [k]
  (clojure.core/swap! *spatialista-session* dissoc k))

(defn assoc-in!
  "Associates a value in the session, where ks is a
   sequence of keys and v is the new value and returns
   a new nested structure. If any levels do not exist,
   hash-maps will be created."
  [ks v]
  (clojure.core/swap! *spatialista-session* #(assoc-in % ks v)))

(defn get!
  "Destructive get from the session. This returns the current value of the key
  and then removes it from the session."
  ([k] (get! k nil))
  ([k default]
   (let [cur (get k default)]
     (remove! k)
     cur)))

(defn get-in!
  "Destructive get from the session. This returns the current value of the path
  specified by the vector ks and then removes it from the session."
  ([ks] (get-in! ks nil))
  ([ks default]
    (let [cur (clojure.core/get-in @*spatialista-session* ks default)]
      (assoc-in! ks nil)
      cur)))

(defn update-in!
  "'Updates' a value in the session, where ks is a
   sequence of keys and f is a function that will
   take the old value along with any supplied args and return
   the new value. If any levels do not exist, hash-maps
   will be created."
  [ks f & args]
  (clojure.core/swap!
    *spatialista-session*
    #(apply (partial update-in % ks f) args)))

(defn ^:private spatialista-session
   "Store spatialista session keys in a :spatialista map, because other middleware that
   expects pure functions may delete keys, and simply merging won't work.
   Ring takes (not (contains? response :session) to mean: don't update session.
   Ring takes (nil? (:session resonse) to mean: delete the session.
   Because spatialista-session mutates :session, it needs to duplicate ring/wrap-session
   functionality to handle these cases."
  [handler]
  (fn [request]
    (binding [*spatialista-session* (atom (clojure.core/get-in request [:session :spatialista] {}))]
      (remove! :_flash)
      (when-let [resp (handler request)]
        (if (=  (clojure.core/get-in request [:session :spatialista] {})  @*spatialista-session*)
          resp
          (if (contains? resp :session)
            (if (nil? (:session resp))
              resp
              (assoc-in resp [:session :spatialista] @*spatialista-session*))
            (assoc resp :session (assoc (:session request) :spatialista @*spatialista-session*))))))))

(defn wrap-spatialista-session
 "A stateful layer around wrap-session. Options are passed to wrap-session."
 [handler & [{:keys [timeout timeout-response] :as opts}]]
 (let [opts (or (dissoc opts :timeout :timeout-response) {})]
   (if timeout
     (-> handler
        (spatialista-session)
        (wrap-idle-session-timeout
         {:timeout timeout
          :timeout-response timeout-response})
        (wrap-session opts))
     (-> handler (spatialista-session) (wrap-session opts)))))

(defn wrap-spatialista-session*
  "A stateful layer around wrap-session. Expects that wrap-session has already
   been used."
  [handler]
  (spatialista-session handler))

;; ## Flash

(declare ^:dynamic *spatialista-flash*)

(defn flash-put!
  "Store a value that will persist for this request and the next."
  [k v]
  (clojure.core/swap! *spatialista-flash* assoc-in [:outgoing k] v))

(defn flash-get
  "Retrieve the flash stored value."
  ([k]
     (flash-get k nil))
  ([k not-found]
   (let [in (clojure.core/get-in @*spatialista-flash* [:incoming k])
         out (clojure.core/get-in @*spatialista-flash* [:outgoing k])]
     (or out in not-found))))

(defn ^:private spatialista-flash [handler]
  (fn [request]
    (binding [*spatialista-flash* (atom {:incoming (:flash request)})]
      (let [resp (handler request)
            outgoing-flash (:outgoing @*spatialista-flash*)]
        (if (and resp outgoing-flash)
          (assoc resp :flash outgoing-flash)
          resp)))))

(defn wrap-spatialista-flash
  "A stateful layer over wrap-flash."
  [handler]
  (-> handler
      (spatialista-flash)
      (wrap-flash)))

(defn wrap-spatialista-flash*
  "A stateful layer over wrap-flash. Expects that wrap-flash has already
   been used."
  [handler]
  (spatialista-flash handler))
