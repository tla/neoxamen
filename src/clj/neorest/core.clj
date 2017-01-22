(ns clj.core
  (:require [clojurewerkz.neocons.rest :as nrest]
            [clojurewerkz.neocons.rest.nodes :as nn]
            [clojurewerkz.neocons.rest.relationships :as nr]
            [clojurewerkz.neocons.rest.labels :as nl])
  (:gen-class))

(def conn (nrest/connect "http://neo4j:nothing@localhost:7474/db/data/"))

(defn get-prop
  "Helper function to retrieve a property from a node or relationship"
  [entity prop]
  (get-in entity [:data prop]))

(defn listdata
  "List a set of nodes according to ID and whatever key(s) are passed"
  [nodelist & listkey]
  (map
    (fn [n] (println (str (:id n) ": "
              (clojure.string/join " / " (map #(get-prop n %) listkey)))))
    nodelist))

;; The class has topics, or units
(defn addTopic
  "Add a topic to the database"
  [label]
  (let [topic (nn/create conn {:label label})]
    (nl/add conn topic :TOPIC)
    (println (str "Added topic " (:id topic) ": " (get-prop topic :label)))))

(defn listTopics
  "Show all topics in the database"
  []
  (listdata (nl/get-all-nodes conn "TOPIC") :label))

;; Questions belong to units
(defn addQuestion
  "Add a question to the database"
  ([tid type text]
  (let [qnode (nn/create conn {:type type :text text})]
    (nl/add conn qnode :QUESTION)
    (nr/create conn (nn/get conn
                      (if (number? tid) tid (Long/parseLong tid)))
                      qnode :has_question)
    (println (str "Added question " (:id qnode) ": " (get-prop qnode :text)))
    qnode))
  ([tid text]
  (addQuestion tid "MC" text)))

(defn listQuestions
  "List the questions currently in the database"
  ;; all of them
  ([]
    (listdata (nl/get-all-nodes conn :QUESTION) :text :type))
  ;; according to a topic
  ([tid]
    (listdata (nn/traverse conn
                (if (number? tid) tid (Long/parseLong tid))
                :relationships [{:direction "out" :type :has_question}] :return-filter {:language "builtin"
                                :name "all_but_start_node"})
              :text :type)))

;; Answers belong to questions
(defn addAnswer
  "Adds an answer to the database for a given question"
  [question text correct]
  (let [anode (nn/create conn {:text text :correct correct})]
    (nl/add conn anode :ANSWER)
    (nr/create conn (nn/get conn question) anode :has_answer)
    anode))

(defn listAnswers
  "List the possible answers for a given question"
  [qid]
  (listdata (nn/traverse conn
              (if (number? qid) qid (Long/parseLong qid))
              :relationships [{:direction "out" :type :has_answer}]
              :return-filter {:language "builtin" :name "all_but_start_node"})
            :text :correct))

(defn -main
  "Experiment with Neo4J"
  [command & args]
  (do (in-ns 'clj.core)
  (apply (resolve (symbol command)) args)))
