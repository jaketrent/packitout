(ns jaketrent.packitout.web.controllers.lists
 (:require
   [clojure.tools.logging :as log]
   [jaketrent.packitout.web.routes.utils :as utils]
   [ring.util.http-response :as http-response]))

(defn create-list!
  [{{:strs [name details date-start date-end]} :form-params :as request}]
  (log/debug "saving list" name details date-start date-end)
  (let [{:keys [query-fn]} (utils/route-data request)
        redir-url "/lists/create"]
    (try
      (if (or (empty? name))
        (cond-> (http-response/found redir-url)
          (empty? name)
          (assoc-in [:flash :errors :name] "name is required"))
        (do
          (query-fn :insert-list! {:name name :details details :date-start date-start :date-end date-end})
          (http-response/found redir-url)))
      (catch Exception e
        (log/error e "failed to create list")
        (-> (http-response/found redir-url)
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))

;; TODO: DRY up
(defn update-list!
  [{{:strs [name details date_start date_end]} :form-params :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        list-id (:list-id (:path-params request))
        redir-url (str "/lists/" list-id "/edit")]
    (log/debug "updating list" list-id name details date_start date_end)
    (try
      (if (or (empty? name))
        (cond-> (http-response/found redir-url)
          (empty? name)
          (assoc-in [:flash :errors :name] "name is required"))
        (do
          (query-fn :update-list! {:id list-id :name name :details details :date-start date_start :date-end date_end})
          (http-response/found redir-url)))
      (catch Exception e
        (log/error e "failed to update list")
        (-> (http-response/found redir-url)
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))

(defn delete-list! [request]
  (let [{:keys [query-fn]} (utils/route-data request)
        list-id (:list-id (:path-params request))
        redir-url "/lists"]
    (log/debug "deleting list" list-id)
    (try
        (query-fn :delete-list! {:id list-id})
        (http-response/found redir-url)
      (catch Exception e
        (log/error e "failed to delete list")
        (-> (http-response/found redir-url)
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))
