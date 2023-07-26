(ns jaketrent.packitout.web.controllers.items
 (:require
   [clojure.tools.logging :as log]
   [jaketrent.packitout.web.routes.utils :as utils]
   [ring.util.http-response :as http-response]))

(defn create-item!
  [{{:strs [name details tags weight]} :form-params :as request}]
  (log/debug "saving item" name details tags weight)
  (let [{:keys [query-fn]} (utils/route-data request)
        redir-url "/items/create"]
    (try
      (if (or (empty? name))
        (cond-> (http-response/found redir-url)
          (empty? name)
          (assoc-in [:flash :errors :name] "name is required"))
        (do
          (query-fn :insert-item! {:name name :details details :tags tags :weight weight})
          (http-response/found redir-url)))
      (catch Exception e
        (log/error e "failed to create item")
        (-> (http-response/found redir-url)
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))

;; TODO: DRY up
(defn update-item!
  [{{:strs [name details tags weight]} :form-params :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        item-id (:item-id (:path-params request))
        redir-url (str "/items/" item-id "/edit")]
    (log/debug "updating item" item-id name details tags weight)
    (try
      (if (or (empty? name))
        (cond-> (http-response/found redir-url)
          (empty? name)
          (assoc-in [:flash :errors :name] "name is required"))
        (do
          (query-fn :update-item! {:id item-id :name name :details details :tags tags :weight weight})
          (http-response/found redir-url)))
      (catch Exception e
        (log/error e "failed to update item")
        (-> (http-response/found redir-url)
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))

(defn delete-item! [request]
  (let [{:keys [query-fn]} (utils/route-data request)
        item-id (:item-id (:path-params request))
        redir-url "/items"]
    (log/debug "deleting item" item-id)
    (try
        (query-fn :delete-item! {:id item-id})
        (http-response/found redir-url)
      (catch Exception e
        (log/error e "failed to delete item")
        (-> (http-response/found redir-url)
            (assoc :flash {:errors {:unknown (.getMessage e)}}))))))
