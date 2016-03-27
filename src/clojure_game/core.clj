(ns clojure-game.core)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

; (use 'clojure-game.core :reload)

(def max-lvl 10)

(defn calc-att
  "given lvl calc att"
  [lvl]
  (* lvl 6))

(defn calc-def
  [lvl]
  (int (* lvl 1.4)))

(defn calc-hp
  [lvl]
  (* lvl 20))

(defn calc-sides
  [lvl]
  (if (> lvl 5) 4 6))

(defn kill-negative
  [n]
  (if (neg? n) 0 n))

(defn calc-base-damage
  [att def]
  (kill-negative (- att def)))

(defn roll-dice
  [sides]
  (inc (rand-int sides)))

(defn create-character
  [name lvl]
  {:name name
   :lvl lvl
   :att (calc-att lvl)
   :def (calc-def lvl)
   :hp (calc-hp lvl)})

(defn real-damage
  [base sides]
  (let [rd (roll-dice sides)
        s (/ sides 2)]
    (cond
      (<= rd s) (int (/ base 2))
      (> rd s) base
      (= rd s) (* base 2))))

(defn take-damage
  [from to]
  (let [bd (calc-base-damage (:att from) (:def to))
        s (calc-sides (:lvl from))
        rd (real-damage bd s)]
    [rd (update-in to [:hp] #(- % rd))]))

(def log-template
  "Character %s recieved %d damage.
  His new live is %d")

(defn print-battle-log
  [damage character]
  (let [name (:name character)
        newhp (:hp character)
        s (format log-template name damage newhp)]
    (println s)))

(defn print-winner
  [p-hp e-hp]
  (if (<= p-hp 0)
    (println "Enemy win")
    (println "Player win")))



(def player (create-character "Robert" 6))
(def troll (create-character "Troll" 2))
(def big-troll (create-character "Big Troll" 3))
(def old-troll (create-character "Old Troll BO" 1))

(def config
  {:player player
   :enemy big-troll})

(defn game-logic
  [config]
  (loop [player (:player config)
         enemy (:enemy config)
         round 1]
    (if (or (<= (:hp player) 0)
          (<= (:hp enemy) 0))
      (print-winner (:hp player) (:hp enemy))
      (let [pl->en (take-damage player enemy)
            en->pl (take-damage player enemy)]
        (do
          (println (str "Raund start: " round))
          (print-battle-log (pl->en 0) (pl->en 1))
          (print-battle-log (en->pl 0) (en->pl 1))
          (recur (en->pl 1) (pl->en 1) (inc round)))))))