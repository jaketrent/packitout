(ns jaketrent.packitout.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[packitout starting]=-"))
   :start      (fn []
                 (log/info "\n-=[packitout started successfully]=-"))
   :stop       (fn []
                 (log/info "\n-=[packitout has shut down successfully]=-"))
   :middleware (fn [handler _] handler)
   :opts       {:profile :prod}})
