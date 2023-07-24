(ns jaketrent.packitout.web.controllers.items
 (:require
   [clojure.tools.logging :as log]
   [jaketrent.packitout.web.routes.utils :as utils]
   [ring.util.http-response :as http-response]))

(defn save-item!
  [{{:strs [name details tags weight]} :form-params :as request}]
  (log/debug "saving item" name details tags weight)
  (let [{:keys [query-fn]} (utils/route-data request)]
    (try
      (if (or (empty? name))
        (cond-> (http-response/found "/items/create")
          (empty? name)
          (assoc-in [:flash :errors :name] "name is required"))
        (do
          (query-fn :insert-item! {:name name :details details :tags tags :weight weight})
          (http-response/found "/items/create")))
      (catch Exception e
        (log/error e "failed to save item")
        (-> (http-response/found "/items/create")
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))
