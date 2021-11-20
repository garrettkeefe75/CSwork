#lang plait

(define-type Value
  (numV [n : Number])
  (closV [arg : (Listof Symbol)]
         [body : Exp]
         [env : Env])
  (boolV [b : Boolean])
  (pairV [fst : Value]
         [snd : Value]))

(define-type Exp
  (numE [n : Number])
  (boolE [b : Boolean])
  (idE [s : Symbol])
  (pairE [fst : Exp]
         [snd : Exp])
  (fstE [pair : Exp])
  (sndE [pair : Exp])
  (plusE [l : Exp] 
         [r : Exp])
  (multE [l : Exp]
         [r : Exp])
  (lamE [n : (Listof Symbol)]
        [arg-types : (Listof Type)]
        [body : Exp])
  (equalE [l : Exp]
          [r : Exp])
  (ifE [test : Exp]
       [than : Exp]
       [else : Exp])
  (appE [fun : Exp]
        [args : (Listof Exp)]))

(define-type Type
  (numT)
  (boolT)
  (crossT [fst : Type]
          [snd : Type])
  (arrowT [args : (Listof Type)]
          [result : Type]))

(define-type Binding
  (bind [name : Symbol]
        [val : Value]))

(define-type-alias Env (Listof Binding))

(define-type Type-Binding
  (tbind [name : Symbol]
         [type : Type]))

(define-type-alias Type-Env (Listof Type-Binding))

(define mt-env empty)
(define extend-env cons)

(module+ test
  (print-only-errors #t))

;; parse ----------------------------------------
(define (parse [s : S-Exp]) : Exp
  (cond
    [(s-exp-match? `NUMBER s) (numE (s-exp->number s))]
    [(s-exp-match? `true s) (boolE #t)]
    [(s-exp-match? `false s) (boolE #f)]
    [(s-exp-match? `SYMBOL s) (idE (s-exp->symbol s))]
    [(s-exp-match? `{pair ANY ANY} s)
     (pairE (parse (second (s-exp->list s)))
            (parse (third (s-exp->list s))))]
    [(s-exp-match? `{fst ANY} s)
     (fstE (parse (second (s-exp->list s))))]
    [(s-exp-match? `{snd ANY} s)
     (sndE (parse (second (s-exp->list s))))]
    [(s-exp-match? `{+ ANY ANY} s)
     (plusE (parse (second (s-exp->list s)))
            (parse (third (s-exp->list s))))]
    [(s-exp-match? `{* ANY ANY} s)
     (multE (parse (second (s-exp->list s)))
            (parse (third (s-exp->list s))))]
    [(s-exp-match? `{let {[SYMBOL : ANY ANY]} ANY} s)
     (let ([bs (s-exp->list (first
                             (s-exp->list (second
                                           (s-exp->list s)))))])
       (appE (lamE (list (s-exp->symbol (first bs)))
                   (list (parse-type (third bs)))
                   (parse (third (s-exp->list s))))
             (list (parse (fourth bs)))))]
    [(s-exp-match? `{lambda {[SYMBOL : ANY] ...} ANY} s)
     (let ([args (s-exp->list
                 (second (s-exp->list s)))]) ; list containing (SYMBOL : ANY)
       (lamE (parse-symbol-list args) ;might need custom function
             (parse-type-list args)
             (parse (third (s-exp->list s)))))]
    [(s-exp-match? `{= ANY ANY} s)
     (equalE (parse (second (s-exp->list s)))
             (parse (third (s-exp->list s))))]
    [(s-exp-match? `{if ANY ANY ANY} s)
     (ifE (parse (second (s-exp->list s)))
          (parse (third (s-exp->list s)))
          (parse (fourth (s-exp->list s))))]
    [(s-exp-match? `{ANY ANY ...} s)
     (appE (parse (first (s-exp->list s)))
           (map parse (rest (s-exp->list s))))]
    [else (error 'parse "invalid input")]))

(define (parse-type [s : S-Exp]) : Type
  (cond
   [(s-exp-match? `num s) 
    (numT)]
   [(s-exp-match? `bool s)
    (boolT)]
   [(s-exp-match? `(ANY ... -> ANY) s)
    (arrowT (reverse (map parse-type (rest (rest (reverse (s-exp->list s))))))
            (parse-type (first (reverse (s-exp->list s)))))]
   [(s-exp-match? `{ANY * ANY} s)
    (crossT (parse-type (first (s-exp->list s)))
            (parse-type (third (s-exp->list s))))]
   [else (error 'parse-type "invalid input")]))

(define (parse-symbol-list [list : (Listof S-Exp)]) : (Listof Symbol)
  (cond
    [(empty? list) empty]
    [else (cons (s-exp->symbol (first (s-exp->list (first list)))) (parse-symbol-list (rest list)))]))

(define (parse-type-list [list : (Listof S-Exp)]) : (Listof Type)
  (cond
    [(empty? list) empty]
    [else (cons (parse-type (third (s-exp->list (first list)))) (parse-type-list (rest list)))]))

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
  (test (parse `{let {[x : num {+ 1 2}]}
                  y})
        (appE (lamE (list 'x) (list (numT)) (idE 'y))
              (list (plusE (numE 1) (numE 2)))))
  (test (parse `{lambda {[x : num]} 9})
        (lamE (list 'x) (list (numT)) (numE 9)))
  (test (parse `{double 9})
        (appE (idE 'double) (list (numE 9))))
  (test/exn (parse `{})
            "invalid input")

  (test (parse-type `num)
        (numT))
  (test (parse-type `bool)
        (boolT))
  (test (parse-type `(num -> bool))
        (arrowT (list (numT)) (boolT)))
  (test/exn (parse-type `1)
            "invalid input"))

;; interp ----------------------------------------
(define (interp [a : Exp] [env : Env]) : Value
  (type-case Exp a
    [(numE n) (numV n)]
    [(boolE b) (boolV b)]
    [(idE s) (lookup s env)]
    [(pairE fst snd)(pairV (interp fst env) (interp snd env))]
    [(fstE pair)
     (pairV-fst (interp pair env))]
    [(sndE pair)
     (pairV-snd (interp pair env))]
    [(plusE l r) (num+ (interp l env) (interp r env))]
    [(multE l r) (num* (interp l env) (interp r env))]
    [(lamE n t body)
     (closV n body env)]
    [(equalE l r)
     (num= (interp l env) (interp r env))]
    [(ifE tst thn els)
     (if (boolV-b (interp tst env))
         (interp thn env)
         (interp els env))]
    [(appE fun args) (type-case Value (interp fun env)
                      [(closV n body c-env)
                       (interp body
                               (bind-all-n n args c-env env))]
                      [else (error 'interp "not a function")])]))

(define (bind-all-n [listS : (Listof Symbol)][listE : (Listof Exp)][env : Env][originEnv : Env]) : Env
  (cond
    [(empty? listS) env]
    [else (extend-env (bind (first listS)(interp (first listE) originEnv)) (bind-all-n (rest listS) (rest listE) env originEnv))]))

(module+ test
  (test (interp (parse `2) mt-env)
        (numV 2))
  (test/exn (interp (parse `x) mt-env)
            "free variable")
  (test (interp (parse `x) 
                (extend-env (bind 'x (numV 9)) mt-env))
        (numV 9))
  (test (interp (parse `{+ 2 1}) mt-env)
        (numV 3))
  (test (interp (parse `{* 2 1}) mt-env)
        (numV 2))
  (test (interp (parse `{+ {* 2 3} {+ 5 8}})
                mt-env)
        (numV 19))
  (test (interp (parse `{lambda {[x : num]} {+ x x}})
                mt-env)
        (closV (list 'x) (plusE (idE 'x) (idE 'x)) mt-env))
  (test (interp (parse `{let {[x : num 5]}
                          {+ x x}})
                mt-env)
        (numV 10))
  (test (interp (parse `{let {[x : num 5]}
                          {let {[x : num {+ 1 x}]}
                            {+ x x}}})
                mt-env)
        (numV 12))
  (test (interp (parse `{let {[x : num 5]}
                          {let {[y : num 6]}
                            x}})
                mt-env)
        (numV 5))
  (test (interp (parse `{{lambda {[x : num]} {+ x x}} 8})
                mt-env)
        (numV 16))
  (test (interp (parse `{if true 4 5})
                mt-env)
         (numV 4))
  
  (test (interp (parse `{if false 4 5})
                mt-env)
         (numV 5))
  
  (test (interp (parse `{if {= 13 {if {= 1 {+ -1 2}}
                                      12
                                      13}}
                            4
                            5})
                mt-env)
         (numV 5))
  (test (interp (parse `{= 13 {if {= 1 {+ -1 2}}
                                     12
                                     13}})
                   mt-env)
         (boolV #f))
  (test (interp (parse `{pair 10 8})
                mt-env)
        ;; Your constructor might be different than pairV:
        (pairV (numV 10) (numV 8)))
  
  (test (interp (parse `{fst {pair 10 8}})
                mt-env)
        (numV 10))
  
  (test (interp (parse `{snd {pair 10 8}})
                mt-env)
        (numV 8))
  
  (test (interp (parse `{let {[p : (num * num) {pair 10 8}]}
                          {fst p}})
                mt-env)
        (numV 10))
  (test (interp (parse `{{lambda {}
                           10}})
                mt-env)
        (numV 10))
  
  (test (interp (parse `{{lambda {[x : num] [y : num]} {+ x y}}
                         10
                         20})
                mt-env)
        (numV 30))
  (test (interp (parse `{let {[f : (num num -> num)
                                 {lambda {[y : num] [z : num]}
                                   {+ z y}}]}
                          {f 1 2}})
                   mt-env)
        (numV 3))

  

  (test/exn (interp (parse `{1 2}) mt-env)
            "not a function")
  (test/exn (interp (parse `{+ 1 {lambda {[x : num]} x}}) mt-env)
            "not a number")
  (test/exn (interp (parse `{let {[bad : (num -> num) {lambda {[x : num]} {+ x y}}]}
                              {let {[y : num 5]}
                                {bad 2}}})
                    mt-env)
            "free variable"))

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
(define (num= [l : Value] [r : Value]) : Value
  (cond
   [(and (numV? l) (numV? r))
      (boolV (equal? (numV-n l) (numV-n r)))]
   [else
    (error 'interp "not a number")]))

(module+ test
  (test (num+ (numV 1) (numV 2))
        (numV 3))
  (test (num* (numV 2) (numV 3))
        (numV 6))
  (test/exn (num= (boolV #t)(numV 9))
            "not a number"))

;; lookup ----------------------------------------
(define (make-lookup [name-of : ('a -> Symbol)] [val-of : ('a -> 'b)])
  (lambda ([name : Symbol] [vals : (Listof 'a)]) : 'b
    (type-case (Listof 'a) vals
      [empty (error 'find "free variable")]
      [(cons val rst-vals) (if (equal? name (name-of val))
                               (val-of (first vals))
                               ((make-lookup name-of val-of) name rst-vals))])))

(define lookup
  (make-lookup bind-name bind-val))

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

;; typecheck ----------------------------------------
(define (typecheck [a : Exp] [tenv : Type-Env]) : Type
  (type-case Exp a
    [(numE n) (numT)]
    [(boolE b) (boolT)]
    [(plusE l r) (typecheck-nums l r tenv)]
    [(multE l r) (typecheck-nums l r tenv)]
    [(idE n) (type-lookup n tenv)]
    [(pairE fst snd)(crossT (typecheck fst tenv)(typecheck snd tenv))]
    [(fstE pair)
     (type-case Type (typecheck pair tenv)
       [(crossT fst snd) fst]
       [else (type-error pair "pair")])]
    [(sndE pair)
     (type-case Type (typecheck pair tenv)
       [(crossT fst snd) snd]
       [else (type-error pair "pair")])]
    [(lamE n arg-type body)
     (arrowT arg-type
             (typecheck body 
                        (tbind-all-n n arg-type tenv)))]
    [(equalE l r)
     (type-case Type (typecheck l tenv)
       [(numT)
        (type-case Type (typecheck r tenv)
          [(numT)(boolT)]
          [else (type-error r "num")])]
       [else (type-error l "num")])]
    [(ifE tst thn els)
     (type-case Type (typecheck tst tenv)
       [(boolT)
        (local [(define thnT (typecheck thn tenv))
                (define elsT (typecheck els tenv))]
          (cond
            [(equal? thnT elsT) thnT]
            [else (type-error thn "else not same type as than")]))]
       [else (type-error tst "bool")])]
    [(appE fun args)
     (type-case Type (typecheck fun tenv)
       [(arrowT arg-type result-type)
        (if (equal? arg-type
                    (typecheck-args args tenv))
            result-type
            (type-error args
                        (to-string arg-type)))]
       [else (type-error fun "function")])]))

(define (typecheck-nums l r tenv)
  (type-case Type (typecheck l tenv)
    [(numT)
     (type-case Type (typecheck r tenv)
       [(numT) (numT)]
       [else (type-error r "num")])]
    [else (type-error l "num")]))

(define (type-error a msg)
  (error 'typecheck (string-append
                     "no type: "
                     (string-append
                      (to-string a)
                      (string-append " not "
                                     msg)))))

(define (tbind-all-n [listS : (Listof Symbol)][listT : (Listof Type)][tenv : Type-Env]) : Type-Env
  (cond
    [(empty? listS) tenv]
    [else (extend-env (tbind (first listS)(first listT))(tbind-all-n (rest listS)(rest listT) tenv))]))

(define (typecheck-args [listE : (Listof Exp)][tenv : Type-Env]) : (Listof Type)
  (cond
    [(empty? listE)empty]
    [else (cons (typecheck (first listE) tenv)(typecheck-args (rest listE) tenv))]))

(define type-lookup
  (make-lookup tbind-name tbind-type))

(module+ test
  (test (typecheck (parse `10) mt-env)
        (numT))
  (test (typecheck (parse `{+ 10 17}) mt-env)
        (numT))
  (test (typecheck (parse `{* 10 17}) mt-env)
        (numT))
  (test (typecheck (parse `{lambda {[x : num]} 12}) mt-env)
        (arrowT (list (numT)) (numT)))
  (test (typecheck (parse `{lambda {[x : num]} {lambda {[y : bool]} x}}) mt-env)
        (arrowT (list (numT)) (arrowT (list (boolT))  (numT))))

  (test (typecheck (parse `{{lambda {[x : num]} 12}
                            {+ 1 17}})
                   mt-env)
        (numT))

  (test (typecheck (parse `{let {[x : num 4]}
                             {let {[f : (num -> num)
                                      {lambda {[y : num]} {+ x y}}]}
                               {f x}}})
                   mt-env)
        (numT))
  (test (typecheck (parse `{= 13 {if {= 1 {+ -1 2}}
                                     12
                                     13}})
                   mt-env)
         (boolT))
  
  (test (typecheck (parse `{if {= 1 {+ -1 2}}
                               {lambda {[x : num]} {+ x 1}}
                               {lambda {[y : num]} y}})
                   mt-env)
        ;; This result may need to be adjusted after part 3:
        (arrowT (list (numT)) (numT)))
  (test (typecheck (parse `{pair 10 8})
                   mt-env)
        ;; Your constructor might be different than crossT:
        (crossT (numT) (numT)))
  
  (test (typecheck (parse `{fst {pair 10 8}})
                   mt-env)
        (numT))
  
  (test (typecheck (parse `{+ 1 {snd {pair 10 8}}})
                   mt-env)
        (numT))
  
  (test (typecheck (parse `{lambda {[x : (num * bool)]}
                             {fst x}})
                   mt-env)
        ;; Your constructor might be different than crossT:
        (arrowT (list (crossT (numT) (boolT))) (numT)))
  
  (test (typecheck (parse `{{lambda {[x : (num * bool)]}
                              {fst x}}
                            {pair 1 false}})
                   mt-env)
        (numT))
  
  (test (typecheck (parse `{{lambda {[x : (num * bool)]}
                              {snd x}}
                            {pair 1 false}})
                   mt-env)
        (boolT))
  (test (typecheck (parse `{{lambda {[x : num] [y : bool]} y}
                            10
                            false})
                   mt-env)
        (boolT))

  (test/exn (typecheck (parse `{= 9 true})
                       mt-env)
            "no type")
  (test/exn (typecheck (parse `{= true 9})
                       mt-env)
            "no type")

  (test/exn (typecheck (parse `{if true 9 false})
                       mt-env)
            "no type")
  
  (test/exn (typecheck (parse `{{lambda {[x : num] [y : bool]} y}
                                false
                                10})
                       mt-env)
            "no type")
  
  (test (typecheck (parse `{let {[x : num 4]}
                             {let {[f : (num num -> num)
                                      {lambda {[y : num] [z : num]}
                                        {+ z y}}]}
                               {f x x}}})
                   mt-env)
        (numT))
  
  (test (typecheck (parse `{let {[x : num 4]}
                             {let {[f : (bool num -> num)
                                      {lambda {[sel : bool] [z : num]}
                                        {if sel x z}}]}
                               {f {= x 5} 0}}})
                   mt-env)
        (numT))
  
  (test/exn (typecheck (parse `{fst 10})
                       mt-env)
            "no type")
  (test/exn (typecheck (parse `{snd {lambda {[x : bool]}
                                    x}})
                       mt-env)
            "no type")
  
  (test/exn (typecheck (parse `{+ 1 {fst {pair false 8}}})
                       mt-env)
            "no type")
  
  (test/exn (typecheck (parse `{lambda {[x : (num * bool)]}
                                 {if {fst x}
                                     1
                                     2}})
                       mt-env)
            "no type")
  
  (test/exn (typecheck (parse `{+ 1 {if true true false}})
                       mt-env)
            "no type")

  (test/exn (typecheck (parse `{1 2})
                       mt-env)
            "no type")
  (test/exn (typecheck (parse `{{lambda {[x : bool]} x} 2})
                       mt-env)
            "no type")
  (test/exn (typecheck (parse `{+ 1 {lambda {[x : num]} x}})
                       mt-env)
            "no type")
  (test/exn (typecheck (parse `{* {lambda {[x : num]} x} 1})
                       mt-env)
            "no type"))