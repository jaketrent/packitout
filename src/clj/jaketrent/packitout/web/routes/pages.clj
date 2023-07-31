(ns jaketrent.packitout.web.routes.pages
  (:require
    [jaketrent.packitout.web.middleware.exception :as exception]
    [jaketrent.packitout.web.pages.layout :as layout]
    [clojure.tools.logging :as log]
    [integrant.core :as ig]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.parameters :as parameters]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [jaketrent.packitout.web.routes.utils :as utils]
    [jaketrent.packitout.web.controllers.items :as items]
    [jaketrent.packitout.web.controllers.lists :as lists]))


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

(defn lists-create [{:keys [flash] :as request}]
  (layout/render request "lists/create.html" {:errors (:errors flash)}))

(defn lists-edit  [{:keys [flash, path-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        list (query-fn :find-list {:id (:list-id path-params)})]
    (layout/render request "lists/edit.html" {:list list
                                              :errors (:errors flash)})))

(defn lists-fill  [{:keys [flash, path-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        list (query-fn :find-list {:id (:list-id path-params)})
        list-items (query-fn :select-list-items {:id (:list-id path-params)})
        total-weight (items/calc-total-weight list-items)
        available-items (query-fn :select-available-items {:date_start (:date_start list) :date_end (:date_end list)})]
    (layout/render request "lists/fill.html" {:available_items available-items
                                              :list_items list-items
                                              :list list
                                              :total-weight total-weight
                                              :errors (:errors flash)})))

(defn lists-fill-add  [{:keys [flash, path-params, form-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        {:strs [item-id]} form-params
        list-id (:list-id path-params)
        ;; TODO: ensure that these are executed serially
        _ (query-fn :insert-list-item! {:list-id list-id :item-id item-id})
        list (query-fn :find-list {:id (:list-id path-params)})
        list-items (query-fn :select-list-items {:id list-id})
        total-weight (items/calc-total-weight list-items)
        available-items (query-fn :select-available-items {:date_start (:date_start list) :date_end (:date_end list)})]
    (layout/render request "lists/fill-lists.html" {:available_items available-items
                                                    :list_items list-items
                                                    :list list
                                                    :total-weight total-weight
                                                    :errors (:errors flash)})))

(defn lists-fill-remove  [{:keys [flash, path-params, form-params] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)
        {:strs [item-id]} form-params
        list-id (:list-id path-params)
        _ (query-fn :delete-list-item! {:list-id list-id :item-id item-id})
        list (query-fn :find-list {:id (:list-id path-params)})
        list-items (query-fn :select-list-items {:id list-id})
        total-weight (items/calc-total-weight list-items)
        available-items (query-fn :select-available-items {:date_start (:date_start list) :date_end (:date_end list)})]
    (layout/render request "lists/fill-lists.html" {:available_items available-items
                                                    :list_items list-items
                                                    :list list
                                                    :total-weight total-weight
                                                    :errors (:errors flash)})))

(defn lists-list [{:keys [flash] :as request}]
  (let [{:keys [query-fn]} (utils/route-data request)]
    (layout/render request "lists/list.html" {:lists (query-fn :select-lists {})
                                              :errors (:errors flash)})))

(defn page-routes [_opts]
  [["/" {:get home}]
   ["/lists" {:get lists-list}]
   ["/lists/create" {:get lists-create :post lists/create-list!}]
   ["/lists/:list-id/edit" {:get lists-edit :post lists/update-list!}]
   ["/lists/:list-id/destroy" {:get lists/delete-list!}]
   ["/lists/:list-id/fill" {:get lists-fill}]
   ["/lists/:list-id/fill/add" {:post lists-fill-add}]
   ["/lists/:list-id/fill/remove" {:post lists-fill-remove}]
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

