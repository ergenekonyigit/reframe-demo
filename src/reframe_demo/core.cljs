(ns reframe-demo.core
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :as str]))


;; A detailed walk-through of this source code is provided in the docs:
;; https://github.com/Day8/re-frame/blob/master/docs/CodeWalkthrough.md

;; -- Domino 1 - Event Dispatch -----------------------------------------------

(defn dispatch-timer-event []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
(defonce do-timer (js/setInterval dispatch-timer-event 1000))

;; -- Domino 2 - Event Handlers -----------------------------------------------

(rf/reg-event-db
 :initialize
 (fn [_ _]
   {:time (js/Date.)
    :time-color "#f34"
    :users [{:name "John" :surname "McCarthy"}]}))

(rf/reg-event-db
 :time-color-change
 (fn [db [_ new-color-value]]
   (assoc db :time-color new-color-value)))

(rf/reg-event-db
 :add-user
 (fn [db [_ user]]
   (update db :users conj user)))

(rf/reg-event-db
 :timer
 (fn [db [_ new-time]]
   (assoc db :time new-time)))

;; -- Domino 4 - Query  -------------------------------------------------------

(rf/reg-sub
 :time
 (fn [db _]
   (:time db)))

(rf/reg-sub
 :time-color
 (fn [db _]
   (:time-color db)))

(rf/reg-sub
 :users
 (fn [db _]
   (:users db)))

;; -- Domino 5 - View Functions ----------------------------------------------

(defn clock []
  [:div
   {:style {:color @(rf/subscribe [:time-color])}}
   (-> @(rf/subscribe [:time])
       .toTimeString
       (str/split " ")
       first)])

(defn color-input []
  [:div {:style {:margin-bottom "20px"}}
   "Time color: "
   [:input
    {:type "text"
     :value @(rf/subscribe [:time-color])
     :on-change #(rf/dispatch [:time-color-change (-> % .-target .-value)])}]])

(defn add-user []
  (let [user (r/atom {:name "" :surname ""})]
    [:<>
     [:div "Add User: "
      [:div
       [:label "Name"]
       [:input
        {:style {:display "block"}
         :type :text
         :id "name"
         :name "name"
         :on-change #(swap! user assoc :name (-> % .-target .-value))}]]
      [:div
       [:label "Surname"]
       [:input
        {:style {:display "block"}
         :type :text
         :id "surname"
         :name "surname"
         :on-change #(swap! user assoc :surname (-> % .-target .-value))}]]
      [:button
       {:on-click #(rf/dispatch [:add-user @user])}
       "Add New User"]]]))

(defn user-list []
  [:ul
   (for [user @(rf/subscribe [:users])]
     ^{:key (str (random-uuid))}
     [:li (:name user) (:surname user)])])

(defn ui []
  [:div
   [:h1 "Hello world, it is now!"]
   [clock]
   [color-input]
   [add-user]
   [user-list]])

(defn mount-root []
  (r/render [ui] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize])
  (mount-root))
