(ns clj-brave-and-true.chapter8)

(def order-details
  {:name  "Mitchard Blimmons"
   :email "mitchard.blimmonsgmail.com"})

(def order-details-validations
  {:name
   ["Please enter a name" not-empty]

   :email
   ["Please enter an email address" not-empty

    "Your email address doesn't look like an email address"
    #(or (empty? %) (re-seq #"@" %))]})

(defn error-messages-for
  "Return a seq of error messages"
  [to-validate message-validator-pairs]
  (map first (filter #(not ((second %) to-validate))
                     (partition 2 message-validator-pairs))))

(defn validate
  "Return a map with a vector of errors for each key"
  [to-validate validations]
  (reduce
   (fn [errors validations]
     (let [[fieldname validation-check-groups] validations
           value (get to-validate fieldname)
           error-messages (error-messages-for value
                                              validation-check-groups)]
       (if (empty? error-messages)
         errors
         (assoc errors fieldname error-messages))))
   {}
   validations))

(defmacro if-valid
  "Handle validation more concisely"
  [to-validate validations errors-name & then-else]
  `(let [~errors-name (validate ~to-validate ~validations)]
     (if (empty? ~errors-name)
       ~@then-else)))

(if-valid order-details order-details-validations errors
          (println "Success!")
          (println "Error!" errors))

;;; 1
(defmacro when-valid
  "Handle validation with when"
  [to-validate validations]
  `(if (empty? (validate ~to-validate ~validations))
     (do
       (println "It's a success!")
       (println ":success"))
     nil))

(when-valid order-details order-details-validations)

;;; 2
(defmacro my-or
  ([] true)
  ([x] x)
  ([x & next]
   `(let [or# ~x]
      (if or#
        or#
        (my-or ~@next)))))

;;; 3
(def character
  {:name "Smooches McCutes"
   :attributes {:intelligence 10
                :strength 4
                :dexterity 5}})

(defmacro defattr
  [func attr]
  `(def ~func (comp ~attr :attributes)))
