#lang plait

(print-only-errors #t)

(define-type Tree
    (leaf [val : Number])
    (node [val : Number]
          [left : Tree]
          [right : Tree]))

(define (sum [t : Tree]) : Number
  (type-case Tree t
      [(leaf v) v]
      [(node v l r) (+ (+ (sum l) (sum r)) v)]))

(test (sum (node 5 (leaf 10) (node 3 (leaf 2) (leaf 1)))) 21)

(test (sum (node 5 (leaf 6) (leaf 7))) 18)

(test (sum (leaf 32)) 32)

(define (negate [t : Tree]) : Tree
  (type-case Tree t
    [(leaf v) (leaf (- 0 v))]
    [(node v l r) (node (- 0 v) (negate l) (negate r))]))

(test (negate (leaf 10)) (leaf -10))

(test (negate (leaf -500)) (leaf 500))

(test (negate (node 5 (leaf 0) (leaf 22))) (node -5 (leaf 0) (leaf -22)))

(define (contains? [t : Tree][n : Number]) : Boolean
  (type-case Tree t
    [(leaf v) (= v n)]
    [(node v l r) (or (= v n) (contains? l n) (contains? r n))]))

(test (contains? (leaf 10) 10) #t)

(test (contains? (node 2 (leaf 1) (leaf 3)) 3) #t)

(test (contains? (node 2 (leaf 1) (leaf 3)) 4) #f)

(define (sum-nodes [t : Tree]) : Number ;Not what I need, but I'll leave it
  (type-case Tree t
    [(leaf v) 0]
    [(node v l r) (+ v (+ (sum-nodes l) (sum-nodes r)))]))

(test (sum-nodes (leaf 5)) 0)

(test (sum-nodes (node 5 (node 2 (leaf 1) (leaf 3)) (leaf 10))) 7)

(define (bigger-leaves? [t : Tree][n : Number]) : Boolean
  (type-case Tree t
    [(leaf v) (> v n)]
    [(node v l r) (and (bigger-leaves? l (+ n v)) (bigger-leaves? r (+ n v)))]))

(define (big-leaves? [t : Tree]) : Boolean
  (bigger-leaves? t 0))

(test (big-leaves? (leaf 10)) #t)

(test (big-leaves? (leaf 0)) #f)

(test (big-leaves? (node 5 (leaf 6) (leaf 7))) #t)

(test (big-leaves? (node 5 (node 2 (leaf 8) (leaf 6)) (leaf 7))) #f)

(define (positive-trees? [l : (Listof Tree)]) : Boolean
  (cond [(not (empty? l)) (and (<= 0 (sum (first l))) (positive-trees? (rest l)))]
        [else #t]))

(test (positive-trees? (cons (leaf 6)
                               empty))
        #t)

(test (positive-trees? (cons (leaf -6)
                               empty))
        #f)

(test (positive-trees? (cons (node 1 (leaf 6) (leaf -6))
                               empty))
        #t)

(test (positive-trees? (cons (node 1 (leaf 6) (leaf -6))
                               (cons (node 0 (leaf 0) (leaf 1))
                                      empty)))
        #t)

(test (positive-trees? (cons (node -1 (leaf 6) (leaf -6))
                               (cons (node 0 (leaf 0) (leaf 1))
                                      empty)))
        #f)
