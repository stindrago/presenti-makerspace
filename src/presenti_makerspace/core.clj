(ns presenti-makerspace.core
  (:refer-clojure :exclude [load])
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.api :as t]
            [yaml.core :as yaml]
            [clojure.java.io :as io])
  (:gen-class))

;; test data
(def dummy-msg {:message_id 317,
                :from {:id 2123314,
                       :is_bot false,
                       :first_name "Alice",
                       :last_name "Alice",
                       :username "Ali",
                       :language_code "en"}
                :chat {:id 25345,
                       :first_name "Alice",
                       :last_name "Alice",
                       :username "Ali",
                       :type "private"}
                :date 1632078528,
                :text "/add Alice Bob 2021-22-22" ,
                :entities [{:offset 0, :length 3, :type "bot_command"}]})

(def dummy-data {:presenti [{"Alice Alice" ["2021-09-13" "2021-09-14"]}
                            {"Bob Bob" ["2021-09-15" "2021-09-16"]}]})

;; back-end
(def whitelist #{703914890 -1001382005418})

(def data-file (io/resource "data.yaml"))

(defn verified? [msg]
  (contains? whitelist (get-in msg [:chat :id])))

(defn read-data [file]
  "Read data from file"
  (yaml/parse-string (slurp file) :keywords false))

(defn parse-presenti [file]
  "Parse the presenti values from the map."
  (get (read-data file) "presenti"))

(defn parse-name [msg]
  "Get the person name you want to add from the message."
  (str (str/join " " (mapv (str/split (:text msg) #"\s+") [1 2]))))

(defn parse-day [msg]
  "Get the day you want to add from the message."
  (str (get (str/split (:text msg) #"\s+") 3)))

(defn add-person [data msg]
  "Add new person entry"
  (let [person (parse-name msg)]
    (conj data {person #{}})))

;; TODO Use sorted-sets instead of a vectors to prevent duplicates and sort the items.
(defn index-of-vector [v name]
  "Pass as `v` `(parse-presenti data-file)` and as `name` the `parse-name` functions."
  (.indexOf v (some #(when (contains? % name) %) v)))

(defn add-day [data msg]
  "Add new day entry"
  (let [name (parse-name msg)
        day (parse-day msg)
        index (index-of-vector data name)]
    (assoc-in data [index name]
              (conj (get-in data [index name]) day))))

;; front-end Telegram
(def token (env :telegram-token))

(defn error? [flag msg]
  "Error messages"
  (cond
    (= flag :pass) (t/send-text token (get-in msg [:chat :id]) "👌 Aggiunto.")
    (= flag :fail-not-verified) (t/send-text token (get-in msg [:chat :id]) "⚠ You are not authorized. This incident will be reported.")
    (= flag :fail-format-error) (t/send-text token (get-in msg [:chat :id]) "⚠ Formato sbagliato.\nUsa `/persona <NOME> <COGNOME>`.")
    (= flag :fail-format-error-day) (t/send-text token (get-in msg [:chat :id]) (str "⚠ Formato giorno sbagliato.\nUsa `/presenza " (parse-name msg) " <YYYY-MM-DD>`."))
    (= flag :fail-person-nil) (t/send-text token (get-in msg [:chat :id]) (str "⚠ Persona inesistente.\nAggiungi persona `/persona " (parse-name msg) "`."))))

(h/defhandler handler

  (h/command-fn "start"
                (fn [{{id :id :as chat} :chat :as msg}]
                  (when (verified? msg)
                    (println "Bot joined new chat: " chat)
                    (t/send-text token id "🎉 Sono arrivatooooooooooooo!!!"))))

  (h/command-fn "help"
                (fn [{{id :id :as chat} :chat :as msg}]
                  (when (verified? msg)
                    (println "Help was requested in: " chat)
                    (t/send-text token id (str "🚑 Chiamo il 118 per @" (get-in msg [:from :username]) ". Aiuti in arrivo.")))))

  (h/command-fn "persona"
                (fn [{{id :id} :chat :as msg}]
                  (cond
                    (not (verified? msg)) (error? :fail-not-verified msg)
                    (not= (count (str/split (:text msg) #"\s+")) 3) (error? :fail-format-error msg)
                    :else (do (println "New person: " (parse-name msg) (parse-day msg))
                              (spit data-file
                                    (yaml/generate-string
                                     (conj {} {"presenti" (add-person (parse-presenti data-file) msg)})))
                              (error? :pass msg)))))

  (h/command-fn "presenza"
                (fn [{{id :id} :chat :as msg}]
                  (cond
                    (not (verified? msg)) (error? :fail-not-verified msg)
                    (not= (count (str/split (:text msg) #"\s+")) 4) (error? :fail-format-error msg)
                    (not (contains? (parse-presenti data-file) (index-of-vector (parse-presenti data-file) (parse-name msg)))) (error? :fail-person-nil msg)
                    (not= (count (parse-day msg)) 10) (error? :fail-format-error-day msg) ;TODO It is better to check string format
                    :else (do (println "New day: " (parse-name msg) (parse-day msg))
                              (spit data-file
                                    (yaml/generate-string
                                     (conj {} {"presenti" (add-day (parse-presenti data-file) msg)})))
                              (error? :pass msg)))))

  (h/command-fn "export"
                (fn [{{id :id} :chat :as msg}]
                  (when (verified? msg)
                    (println "Export: " data-file)
                    (t/send-text token id "⏳ Esportazione in corso...")
                    (t/send-document token id (io/file data-file)))))

  (h/message-fn
   (fn [{{id :id} :chat :as msg}]
     (when (verified? msg)
       (println "Intercepted message: " msg)
       (t/send-text token id "ℹ Ti serve aiuto? Digita /help.")))))

(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provide token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))

  (println "Starting the bot.")
  (<!! (p/start token handler)))
