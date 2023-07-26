(ns jaketrent.packitout.web.routes.pages
  (:require
    [jaketrent.packitout.web.middleware.exception :as exception]
    [jaketrent.packitout.web.pages.layout :as layout]
    [integrant.core :as ig]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [jaketrent.packitout.web.routes.utils :as utils]
    [jaketrent.packitout.web.controllers.items :as items]))


(defn wrap-page-defaults []
  (let [error-page (layout/error-page
                     {:status 403
                      :title "Invalid anti-forgery token"})]
    #(wrap-anti-forgery % {:error-response error-page})))

(defn home [request]
  (layout/render request "home.html"))

(defn items-create [{:keys [flash] :as request}]
  (layout/render request "items/create.html" {:errors (:errors flash)}))

(defn items-edit  [{:keys [flash, path-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "items/edit.html" {:item (query-fn :find-item {:id (:item-id path-params)})
                                              :errors (:errors flash)})))

(defn items-list [{:keys [flash] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "items/list.html" {:items (query-fn :select-items {})
                                              :errors (:errors flash)})))

(defn page-routes [_opts]
  [["/" {:get home}]
   ["/items" {:get items-list}]
   ["/items/create" {:get items-create :post items/create-item!}]
   ["/items/:item-id/edit" {:get items-edit :post items/update-item!}]
   ["/items/:item-id/destroy" {:get items/delete-item!}]])

(defn route-data [opts]
  (merge
   opts
   {:middleware 
    [;; Default middleware for pages
     (wrap-page-defaults)
     ;; query-params & form-params
     parameters/parameters-middleware
     ;; encoding response body
     muuntaja/format-response-middleware
     ;; exception handling
     exception/wrap-exception]}))

(derive :reitit.routes/pages :reitit/routes)

(defmethod ig/init-key :reitit.routes/pages
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  (layout/init-selmer!)
  [base-path (route-data opts) (page-routes opts)])

