#lang plait

(define-type-alias Location Number)

(define-type Value
  (numV [n : Number])
  (closV [arg : Symbol]
         [body : Exp]
         [env : Env])
  (boxV [l : Location])
  (recV [ns : (Listof Symbol)]
        [vs : (Listof Value)]))

(define-type Exp
  (numE [n : Number])
  (idE [s : Symbol])
  (plusE [l : Exp] 
         [r : Exp])
  (multE [l : Exp]
         [r : Exp])
  (letE [n : Symbol] 
        [rhs : Exp]
        [body : Exp])
  (lamE [n : Symbol]
        [body : Exp])
  (appE [fun : Exp]
        [arg : Exp])
  (recordE [ns : (Listof Symbol)]
           [args : (Listof Exp)])
  (getE [rec : Exp]
        [n : Symbol])
  (boxE [arg : Exp])
  (unboxE [arg : Exp])
  (setboxE [bx : Exp]
           [val : Exp])
  (beginE [list : (Listof Exp)]))

(define-type Binding
  (bind [name : Symbol]
        [val : Value]))

(define-type-alias Env (Listof Binding))

(define mt-env empty)
(define extend-env cons)

(define-type Storage
  (cell [location : Location] 
        [val : Value]))

(define-type-alias Store (Listof Storage))
(define mt-store empty)
(define override-store cons)

(define-type Result
  (v*s [v : Value] [s : Store]))

(module+ test
  (print-only-errors #t))

;; parse ----------------------------------------
(define (parse [s : S-Exp]) : Exp
  (cond
    [(s-exp-match? `NUMBER s) (numE (s-exp->number s))]
    [(s-exp-match? `SYMBOL s) (idE (s-exp->symbol s))]
    [(s-exp-match? `{+ ANY ANY} s)
     (plusE (parse (second (s-exp->list s)))
            (parse (third (s-exp->list s))))]
    [(s-exp-match? `{* ANY ANY} s)
     (multE (parse (second (s-exp->list s)))
            (parse (third (s-exp->list s))))]
    [(s-exp-match? `{let {[SYMBOL ANY]} ANY} s)
     (let ([bs (s-exp->list (first
                             (s-exp->list (second
                                           (s-exp->list s)))))])
       (letE (s-exp->symbol (first bs))
             (parse (second bs))
             (parse (third (s-exp->list s)))))]
    [(s-exp-match? `{record {SYMBOL ANY} ...} s)
     (recordE (map (lambda (l) (s-exp->symbol (first (s-exp->list l))))
                   (rest (s-exp->list s)))
              (map (lambda (l) (parse (second (s-exp->list l))))
                   (rest (s-exp->list s))))]
    [(s-exp-match? `{get ANY SYMBOL} s)
     (getE (parse (second (s-exp->list s)))
           (s-exp->symbol (third (s-exp->list s))))]
    [(s-exp-match? `{lambda {SYMBOL} ANY} s)
     (lamE (s-exp->symbol (first (s-exp->list 
                                  (second (s-exp->list s)))))
           (parse (third (s-exp->list s))))]
    [(s-exp-match? `{box ANY} s)
     (boxE (parse (second (s-exp->list s))))]
    [(s-exp-match? `{unbox ANY} s)
     (unboxE (parse (second (s-exp->list s))))]
    [(s-exp-match? `{set-box! ANY ANY} s)
     (setboxE (parse (second (s-exp->list s)))
              (parse (third (s-exp->list s))))]
    [(s-exp-match? `{begin ANY ...} s)
     (beginE (map parse (rest (s-exp->list s))))]
    [(s-exp-match? `{ANY ANY} s)
     (appE (parse (first (s-exp->list s)))
           (parse (second (s-exp->list s))))]
    [else (error 'parse "invalid input")]))


(module+ test
  (test (parse `2)
        (numE 2))
  (test (parse `x)
        (idE 'x))
  (test (parse `{+ 2 1})
        (plusE (numE 2) (numE 1)))
  (test (parse `{* 3 4})
        (multE (numE 3) (numE 4)))
  (test (parse `{+ {* 3 4} 8})
        (plusE (multE (numE 3) (numE 4))
               (numE 8)))
  (test (parse `{let {[x {+ 1 2}]}
                  y})
        (letE 'x (plusE (numE 1) (numE 2))
              (idE 'y)))
  (test (parse `{lambda {x} 9})
        (lamE 'x (numE 9)))
  (test (parse `{double 9})
        (appE (idE 'double) (numE 9)))
  (test (parse `{box 0})
        (boxE (numE 0)))
  (test (parse `{unbox b})
        (unboxE (idE 'b)))
  (test (parse `{set-box! b 0})
        (setboxE (idE 'b) (numE 0)))
  (test (parse `{begin 1 2})
        (beginE (cons (numE 1) (cons (numE 2) empty))))
  (test/exn (parse `{{+ 1 2}})
            "invalid input"))

;; with form ----------------------------------------
(define-syntax-rule
  (with [(v-id sto-id) call]
    body)
  (type-case Result call
    [(v*s v-id sto-id) body]))
                                
;; interp ----------------------------------------
(define (interp [a : Exp] [env : Env] [sto : Store]) : Result
  (type-case Exp a
    [(numE n) (v*s (numV n) sto)]
    [(idE s) (v*s (lookup s env) sto)]
    [(plusE l r)
     (with [(v-l sto-l) (interp l env sto)]
       (with [(v-r sto-r) (interp r env sto-l)]
         (v*s (num+ v-l v-r) sto-r)))]
    [(multE l r)
     (with [(v-l sto-l) (interp l env sto)]
       (with [(v-r sto-r) (interp r env sto-l)]
         (v*s (num* v-l v-r) sto-r)))]
    [(letE n rhs body)
     (with [(v-rhs sto-rhs) (interp rhs env sto)]
       (interp body
               (extend-env
                (bind n v-rhs)
                env)
               sto-rhs))]
    [(lamE n body)
     (v*s (closV n body env) sto)]
    [(appE fun arg)
     (with [(v-f sto-f) (interp fun env sto)]
       (with [(v-a sto-a) (interp arg env sto-f)]
         (type-case Value v-f
           [(closV n body c-env)
            (interp body
                    (extend-env
                     (bind n v-a)
                     c-env)
                    sto-a)]
           [else (error 'interp "not a function")])))]
    [(recordE ns as)
     (v*s (recV ns (fst (get-values as env sto))) (snd (get-values as env sto)))]
    [(getE a n)
     (with [(v-a sto-a) (interp a env sto)]
           (type-case Value v-a
             [(recV ns vs) (v*s (find n ns vs) sto-a)]
             [else (error 'interp "not a record")]))]
    [(boxE a)
     (with [(v sto-v) (interp a env sto)]
       (let ([l (new-loc sto-v)])
         (v*s (boxV l) 
              (override-store (cell l v) 
                              sto-v))))]
    [(unboxE a)
     (with [(v sto-v) (interp a env sto)]
       (type-case Value v
         [(boxV l) (v*s (fetch l sto-v) 
                        sto-v)]
         [else (error 'interp "not a box")]))]
    [(setboxE bx val)
     (with [(v-b sto-b) (interp bx env sto)]
       (with [(v-v sto-v) (interp val env sto-b)]
         (type-case Value v-b
           [(boxV l)
            (v*s v-v
                 (replace-cell sto-v l v-v))]
           [else (error 'interp "not a box")])))]
    [(beginE l)
     (cond
       [(empty? (rest l)) (interp (first l) env sto)]
       [else
        (with [(v-l sto-l) (interp (first l) env sto)]
              (interp (beginE (rest l)) env sto-l))])]))

(define (get-values [as : (Listof Exp)][env : (Listof Binding)][sto : (Listof Storage)])
  : ((Listof Value) * (Listof Storage))
  (cond
       [(empty? as) (pair empty sto)]
       [else (with [(v-as sto-as)(interp (first as) env sto)]
                   (pair (cons v-as (fst (get-values (rest as) env sto-as)))
                         (snd (get-values (rest as) env sto-as))))]))

(define (replace-cell [list : (Listof Storage)][loc : Location][v : Value]) : (Listof Storage)
  (cond
    [(empty? list) list]
    [(equal? loc (cell-location (first list))) (cons (cell loc v)(rest list))]
    [else (cons (first list) (replace-cell (rest list) loc v))]))

;; Takes a name and two parallel lists, returning an item from the
;; second list where the name matches the item from the first list.
(define (find [n : Symbol] [ns : (Listof Symbol)] [vs : (Listof Value)])
  : Value
  (cond
   [(empty? ns) (error 'interp "no such field")]
   [else (if (symbol=? n (first ns))
             (first vs)
             (find n (rest ns) (rest vs)))]))

(module+ test
  (test (replace-cell (override-store (cell 1 (numV 15)) mt-store) 1 (numV 6))
        (override-store (cell 1 (numV 6)) mt-store))
  (test (replace-cell (override-store (cell 2 (numV 10))(override-store (cell 1 (numV 15)) mt-store)) 1 (numV 8))
        (override-store (cell 2 (numV 10))(override-store (cell 1 (numV 8)) mt-store)))
  (test (replace-cell mt-store 1 (numV 6))
        empty)
  (test (get-values (cons (multE (numE 1) (numE 8)) empty) mt-env mt-store)
        (pair (cons (numV 8) empty) mt-store))
  (test (interp (parse `2) mt-env mt-store)
        (v*s (numV 2) 
             mt-store))
  (test/exn (interp (parse `x) mt-env mt-store)
            "free variable")
  (test (interp (parse `x) 
                (extend-env (bind 'x (numV 9)) mt-env)
                mt-store)
        (v*s (numV 9)
             mt-store))
  (test (interp (parse `{+ 2 1}) mt-env mt-store)
        (v*s (numV 3)
             mt-store))
  (test (interp (parse `{* 2 1}) mt-env mt-store)
        (v*s (numV 2)
             mt-store))
  (test (interp (parse `{+ {* 2 3} {+ 5 8}})
                mt-env
                mt-store)
        (v*s (numV 19)
             mt-store))
  (test (interp (parse `{lambda {x} {+ x x}})
                mt-env
                mt-store)
        (v*s (closV 'x (plusE (idE 'x) (idE 'x)) mt-env)
             mt-store))
  (test (interp (parse `{let {[x 5]}
                          {+ x x}})
                mt-env
                mt-store)
        (v*s (numV 10)
             mt-store))
  (test (interp (parse `{let {[x 5]}
                          {let {[x {+ 1 x}]}
                            {+ x x}}})
                mt-env
                mt-store)
        (v*s (numV 12)
             mt-store))
  (test (interp (parse `{let {[x 5]}
                          {let {[y 6]}
                            x}})
                mt-env
                mt-store)
        (v*s (numV 5)
             mt-store))
  (test (interp (parse `{{lambda {x} {+ x x}} 8})
                mt-env
                mt-store)
        (v*s (numV 16)
             mt-store))
  (test (interp (parse `{box 5})
                mt-env
                mt-store)
        (v*s (boxV 1)
             (override-store (cell 1 (numV 5))
                             mt-store)))
  (test (interp (parse `{unbox {box 5}})
                mt-env
                mt-store)
        (v*s (numV 5)
             (override-store (cell 1 (numV 5))
                             mt-store)))
  (test (interp (parse `{set-box! {box 5} 6})
                mt-env
                mt-store)
        (v*s (numV 6)
             (override-store (cell 1 (numV 6)) mt-store)))
  (test (interp (parse `{let {[b {box 1}]}
                          {begin
                           {set-box! b 2}
                           {unbox b}}})
                mt-env
                mt-store)
        (v*s (numV 2)
             (override-store (cell 1 (numV 2))
                             mt-store)))
  (test (interp (parse `{begin 1 2})
                mt-env
                mt-store)
        (v*s (numV 2)
             mt-store))
  (test (interp (parse `{let {[b {box 1}]}
                          {begin
                            {set-box! b {+ 2 {unbox b}}}
                            {set-box! b {+ 3 {unbox b}}}
                            {set-box! b {+ 4 {unbox b}}}
                            {unbox b}}})
                mt-env
                mt-store)
        (v*s (numV 10)
             (override-store (cell 1 (numV 10))
                             mt-store)))
  (test (interp (parse `{let {[b {box 5}]}
                          {begin
                            {set-box! b 6}
                            {unbox b}}})
                mt-env
                mt-store)
        (v*s (numV 6)
             (override-store (cell 1 (numV 6)) mt-store)))

  (test/exn (interp (parse `{1 2}) mt-env mt-store)
            "not a function")
  (test/exn (interp (parse `{+ 1 {lambda {x} x}}) mt-env mt-store)
            "not a number")
  (test/exn (interp (parse `{unbox 1}) mt-env mt-store)
            "not a box")
  (test/exn (interp (parse `{set-box! 1 2}) mt-env mt-store)
            "not a box")
  (test/exn (interp (parse `{let {[bad {lambda {x} {+ x y}}]}
                              {let {[y 5]}
                                {bad 2}}})
                    mt-env
                    mt-store)
            "free variable")
  (test/exn (interp (parse `{get 10 b}) mt-env mt-store)
            "not a record"))

;; num+ and num* ----------------------------------------
(define (num-op [op : (Number Number -> Number)] [l : Value] [r : Value]) : Value
  (cond
   [(and (numV? l) (numV? r))
    (numV (op (numV-n l) (numV-n r)))]
   [else
    (error 'interp "not a number")]))
(define (num+ [l : Value] [r : Value]) : Value
  (num-op + l r))
(define (num* [l : Value] [r : Value]) : Value
  (num-op * l r))

(module+ test
  (test (num+ (numV 1) (numV 2))
        (numV 3))
  (test (num* (numV 2) (numV 3))
        (numV 6)))

;; lookup ----------------------------------------
(define (lookup [n : Symbol] [env : Env]) : Value
  (type-case (Listof Binding) env
   [empty (error 'lookup "free variable")]
   [(cons b rst-env) (cond
                       [(symbol=? n (bind-name b))
                        (bind-val b)]
                       [else (lookup n rst-env)])]))

(module+ test
  (test/exn (lookup 'x mt-env)
            "free variable")
  (test (lookup 'x (extend-env (bind 'x (numV 8)) mt-env))
        (numV 8))
  (test (lookup 'x (extend-env
                    (bind 'x (numV 9))
                    (extend-env (bind 'x (numV 8)) mt-env)))
        (numV 9))
  (test (lookup 'y (extend-env
                    (bind 'x (numV 9))
                    (extend-env (bind 'y (numV 8)) mt-env)))
        (numV 8)))
  
;; store operations ----------------------------------------

(define (new-loc [sto : Store]) : Location
  (+ 1 (max-address sto)))

(define (max-address [sto : Store]) : Location
  (type-case (Listof Storage) sto
   [empty 0]
   [(cons c rst-sto) (max (cell-location c)
                          (max-address rst-sto))]))

(define (fetch [l : Location] [sto : Store]) : Value
  (type-case (Listof Storage) sto
   [empty (error 'interp "unallocated location")]
   [(cons c rst-sto) (if (equal? l (cell-location c))
                         (cell-val c)
                         (fetch l rst-sto))]))

(module+ test
  (test (max-address mt-store)
        0)
  (test (max-address (override-store (cell 2 (numV 9))
                                     mt-store))
        2)
  
  (test (fetch 2 (override-store (cell 2 (numV 9))
                                 mt-store))
        (numV 9))
  (test (fetch 2 (override-store (cell 2 (numV 10))
                                 (override-store (cell 2 (numV 9))
                                                 mt-store)))
        (numV 10))
  (test (fetch 3 (override-store (cell 2 (numV 10))
                                 (override-store (cell 3 (numV 9))
                                                 mt-store)))
        (numV 9))
  (test/exn (fetch 2 mt-store)
            "unallocated location"))

;; interp-expr ------------------------------------------
(define (interp-expr [a : Exp]) : S-Exp
  (with [(v-a sto-a) (interp a mt-env mt-store)]
        (type-case Value v-a
          [(recV ns vs) `record]
          [(numV v) (number->s-exp v)]
          [(closV arg body env) `function]
          [(boxV l) `box])))

(module+ test
  (test (interp-expr (parse `{+ 1 4}))
        `5)
  (test (interp-expr (parse `{record {a 10} {b {+ 1 2}}}))
        `record)
  (test (interp-expr (parse `{get {record {a 10} {b {+ 1 0}}} b}))
        `1)
  (test/exn (interp-expr (parse `{get {record {a 10}} b}))
            "no such field")
  (test (interp-expr (parse `{get {record {r {record {z 0}}}} r}))
        `record)
  (test (interp-expr (parse `{get {get {record {r {record {z 0}}}} r} z}))
        `0)
  (test (interp-expr (parse `{let {[b {box 0}]}
                               {let {[r {record {a {unbox b}}}]}
                                 {begin
                                   {set-box! b 1}
                                   {get r a}}}}))
        `0)
  (test (interp-expr (parse `{box 0}))
        `box)
  (test (interp-expr (parse `{lambda {x} {+ x x}}))
        `function)
  (test (interp-expr (parse `{let {[z {box 1}]} {begin {record {x {set-box! z 2}}} {unbox z}}}))
        `2)
  (test (interp-expr (parse `{let {[b {box 0}]}{let {[r {record {a {unbox b}}}]}{begin{set-box! b 1}{get r a}}}}))
        `0)
  (test (interp-expr (parse `{let {[z {box 1}]} {let {[r {record {x 1}}]}
                                                  {begin {get {begin {set-box! z 2} r} x} {unbox z}}}}))
        `2))


