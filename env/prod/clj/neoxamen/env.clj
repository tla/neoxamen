(ns neoxamen.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[neoxamen started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[neoxamen has shut down successfully]=-"))
   :middleware identity})
