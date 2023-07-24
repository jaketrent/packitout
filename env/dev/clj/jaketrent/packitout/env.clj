(ns jaketrent.packitout.env
  (:require
    [clojure.tools.logging :as log]
    [jaketrent.packitout.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init       (fn []
                 (log/info "\n-=[packitout starting using the development or test profile]=-"))
   :start      (fn []
                 (log/info "\n-=[packitout started successfully using the development or test profile]=-"))
   :stop       (fn []
                 (log/info "\n-=[packitout has shut down successfully]=-"))
   :middleware wrap-dev
   :opts       {:profile       :dev
                :persist-data? true}})
