(ns user
  (:require [mount.core :as mount]
            neoxamen.core))

(defn start []
  (mount/start-without #'neoxamen.core/http-server
                       #'neoxamen.core/repl-server))

(defn stop []
  (mount/stop-except #'neoxamen.core/http-server
                     #'neoxamen.core/repl-server))

(defn restart []
  (stop)
  (start))


