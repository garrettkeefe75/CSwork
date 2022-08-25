#lang plait

(define-type Exp
  (numE [n : Number])
  (plusE [lhs : Exp]
         [rhs : Exp])
  (multE [lhs : Exp]
         [rhs : Exp])
  (argE)
  (thisE)
  (idE [s : Symbol])
  (newE [class-name : Symbol]
        [args : (Listof Exp)])
  (getE [obj-expr : Exp]
        [field-name : Symbol])
  (sendE [obj-expr : Exp]
         [method-name : Symbol]
         [arg-expr : Exp])
  (ssendE [obj-expr : Exp]
          [class-name : Symbol]
          [method-name : Symbol]
          [arg-expr : Exp])
  (letE [name : Symbol]
        [val : Exp]
        [body : Exp])
  (if0E [test-expr : Exp]
        [then-expr : Exp]
        [else-expr : Exp])
  (newarrayE [type : Symbol]
             [size : Exp]
             [elem : Exp])
  (arrayrefE [array : Exp]
             [index : Exp])
  (arraysetE [array : Exp]
             [index : Exp]
             [elem : Exp])
  (nulE))

(define-type Binding
  (bind [name : Symbol]
        [val : Value]))

(define-type-alias Env (Listof Binding))

(define mt-env empty)
(define extend-env cons)

(define-type Class
  (classC [field-names : (Listof Symbol)]
          [methods : (Listof (Symbol * Exp))]))

(define-type Value
  (numV [n : Number])
  (objV [class-name : Symbol]
        [field-values : (Listof Value)])
  (nulV)
  (arrayV [type : Symbol]
          [list : (Boxof (Listof Exp))]))

(module+ test
  (print-only-errors #t))

;; ----------------------------------------

(define (find [l : (Listof (Symbol * 'a))] [name : Symbol]) : 'a
  (type-case (Listof (Symbol * 'a)) l
    [empty
     (error 'find (string-append "not found: " (symbol->string name)))]
    [(cons p rst-l)
     (if (symbol=? (fst p) name)
         (snd p)
         (find rst-l name))]))

(module+ test
  (test (find (list (values 'a 1)) 'a)
        1)
  (test (find (list (values 'a 1) (values 'b 2)) 'b)
        2)
  (test/exn (find empty 'a)
            "not found: a")
  (test/exn (find (list (values 'a 1)) 'x)
            "not found: x"))

;; ----------------------------------------

(define interp : (Exp (Listof (Symbol * Class)) Value Value Env -> Value)
  (lambda (a classes this-val arg-val env)
    (local [(define (recur expr)
              (interp expr classes this-val arg-val env))]
      (type-case Exp a
        [(numE n) (numV n)]
        [(nulE) (nulV)]
        [(plusE l r) (num+ (recur l) (recur r))]
        [(multE l r) (num* (recur l) (recur r))]
        [(thisE) this-val]
        [(argE) arg-val]
        [(idE s) (lookup s env)]
        [(newE class-name field-exprs)
         (local [(define c (find classes class-name))
                 (define vals (map recur field-exprs))]
           (if (= (length vals) (length (classC-field-names c)))
               (objV class-name vals)
               (error 'interp "wrong field count")))]
        [(getE obj-expr field-name)
         (type-case Value (recur obj-expr)
           [(objV class-name field-vals)
            (type-case Class (find classes class-name)
              [(classC field-names methods)
               (find (map2 (lambda (n v) (values n v))
                           field-names
                           field-vals)
                     field-name)])]
           [else (error 'interp "not an object")])]
        [(sendE obj-expr method-name arg-expr)
         (local [(define obj (recur obj-expr))
                 (define arg-val (recur arg-expr))]
           (type-case Value obj
             [(objV class-name field-vals)
              (call-method class-name method-name classes
                           obj arg-val env)]
             [else (error 'interp "not an object")]))]
        [(ssendE obj-expr class-name method-name arg-expr)
         (local [(define obj (recur obj-expr))
                 (define arg-val (recur arg-expr))]
           (call-method class-name method-name classes
                        obj arg-val env))]
        [(letE name val body)
         (interp body classes this-val arg-val (extend-env (bind name (recur val)) env))]
        [(if0E test-expr then-expr else-expr)
         (local [(define test (recur test-expr))]
           (if (equal? test (numV 0))
               (recur then-expr)
               (recur else-expr)))]
        [(newarrayE type size elem)
         (arrayV type (box (make-array elem (numV-n (recur size)))))]
        [(arrayrefE array index)
         (local [(define array-v (recur array))
                 (define list (unbox (arrayV-list array-v)))
                 (define idx (numV-n (recur index)))]
           (if (> (length list) idx)
               (recur (list-ref list idx))
               (error 'interp "Given index larger than array size.")))]
        [(arraysetE array index elem)
         (local [(define array-v (recur array))
                 (define list (unbox (arrayV-list array-v)))
                 (define idx (numV-n (recur index)))]
           (begin
             (if (> (length list) idx)
                 (set-box! (arrayV-list array-v)(alter-elem list elem idx empty)) ;;need to find way to set arrayV-list
                 (error 'interp "Given index larger than array size."))
             (numV 0)))]))))

(define (alter-elem [list : (Listof Exp)] [elem : Exp] [index : Number] [first-elems : (Listof Exp)])
  (if (equal? index 0)
      (append (reverse first-elems) (cons elem (rest list)))
      (alter-elem (rest list) elem (- index 1) (cons (first list) first-elems))))

(module+ test
  (test (alter-elem (cons (numE 1) (cons (numE 2) (cons (numE 3) empty))) (numE 4) 2 empty)
        (cons (numE 1) (cons (numE 2) (cons (numE 4) empty)))))

(define (make-array [elem : Exp] [size : Number])
  (cond
    [(equal? size 0) empty]
    [else (cons elem (make-array elem (- size 1)))]))

(define (lookup [name : Symbol] [env : (Listof Binding)]) : Value
  (cond
    [(empty? env) (error 'lookup "no such binding")]
    [(equal? name (bind-name (first env))) (bind-val (first env))]
    [else (lookup name (rest env))]))

(define (call-method class-name method-name classes
                     obj arg-val env)
  (type-case Class (find classes class-name)
    [(classC field-names methods)
     (let ([body-expr (find methods method-name)])
       (interp body-expr
               classes
               obj
               arg-val
               env))]))

(define (num-op [op : (Number Number -> Number)]
                [op-name : Symbol] 
                [x : Value]
                [y : Value]) : Value
  (cond
    [(and (numV? x) (numV? y))
     (numV (op (numV-n x) (numV-n y)))]
    [else (error 'interp "not a number")]))

(define (num+ x y) (num-op + '+ x y))
(define (num* x y) (num-op * '* x y))

;; ----------------------------------------
;; Examples

(module+ test
  (define posn-class
    (values 'Posn
            (classC 
             (list 'x 'y)
             (list (values 'mdist
                           (plusE (getE (thisE) 'x) (getE (thisE) 'y)))
                   (values 'addDist
                           (plusE (sendE (thisE) 'mdist (numE 0))
                                  (sendE (argE) 'mdist (numE 0))))
                   (values 'addX
                           (plusE (getE (thisE) 'x) (argE)))
                   (values 'multY (multE (argE) (getE (thisE) 'y)))
                   (values 'factory12 (newE 'Posn (list (numE 1) (numE 2))))))))
    
  (define posn3D-class
    (values 'Posn3D
            (classC 
             (list 'x 'y 'z)
             (list (values 'mdist (plusE (getE (thisE) 'z)
                                         (ssendE (thisE) 'Posn 'mdist (argE))))
                   (values 'addDist (ssendE (thisE) 'Posn 'addDist (argE)))))))

  (define null-class
    (values 'NullClass
            (classC
             (list 'z)
             (list (values 'test (getE (thisE) 'z))))))

  (define posn27 (newE 'Posn (list (numE 2) (numE 7))))
  (define posn531 (newE 'Posn3D (list (numE 5) (numE 3) (numE 1))))
  (define new-Null (newE 'NullClass (list (nulE))))

  (define (interp-posn a)
    (interp a (list posn-class posn3D-class) (numV -1) (numV -1) mt-env)))

;; ----------------------------------------

(module+ test
  (test (interp (numE 10) 
                empty (objV 'Object empty) (numV 0) mt-env)
        (numV 10))
  (test (interp (letE 'x (numE 52) (idE 'x)) 
                empty (objV 'Object empty) (numV 0) mt-env)
        (numV 52))
  (test (interp (letE 'y (numE 100) (letE 'x (numE 50) (plusE (idE 'x) (idE 'y)))) 
                empty (objV 'Object empty) (numV 0) mt-env)
        (numV 150))
  (test (interp (nulE)
                empty (objV 'Object empty) (numV 0) mt-env)
        (nulV))
  (test (interp (newarrayE 'num (numE 5) (numE 10))
                empty (objV 'Object empty) (numV 0) mt-env)
        (arrayV 'num (box (cons (numE 10) (cons (numE 10) (cons (numE 10) (cons (numE 10) (cons (numE 10) empty))))))))
  (test (interp (arrayrefE (newarrayE 'num (numE 5) (numE 10)) (numE 4))
                empty (objV 'Object empty) (numV 0) mt-env)
        (numV 10))
  (test (interp (arraysetE (newarrayE 'num (numE 5) (numE 10)) (numE 4) (numE 32)) 
                empty (objV 'Object empty) (numV 0) mt-env)
        (numV 0))
  (test (interp (letE 'x (newarrayE 'num (numE 5) (numE 10)) (if0E (arraysetE (idE 'x) (numE 4) (numE 32)) (arrayrefE (idE 'x) (numE 4)) (numE -1))) 
                empty (objV 'Object empty) (numV 0) mt-env)
        (numV 32)) ;; Imperative test here
  (test (interp (plusE (numE 10) (numE 17))
                empty (objV 'Object empty) (numV 0) mt-env)
        (numV 27))
  (test (interp (multE (numE 10) (numE 7))
                empty (objV 'Object empty) (numV 0) mt-env)
        (numV 70)) 

  (test (interp (newE 'NullClass (list new-Null))
                (list null-class) (numV -1) (numV -1) mt-env)
        (objV 'NullClass (list (objV 'NullClass (list (nulV))))))

  (test (interp (getE new-Null 'z)
                (list null-class) (numV -1) (numV -1) mt-env)
        (nulV))

  (test (interp (sendE new-Null 'test (nulE))
                (list null-class) (numV -1) (numV -1) mt-env)
        (nulV))

  (test (interp (if0E (numE 0) (newE 'NullClass (list new-Null)) new-Null)
                (list null-class) (numV -1) (numV -1) mt-env)
        (objV 'NullClass (list (objV 'NullClass (list (nulV))))))

  (test (interp (if0E (numE 1) (plusE (numE 20) (numE 2)) (multE (numE 10) (numE 50)))
                empty (numV -1) (numV -1) mt-env)
        (numV 500))

  (test (interp-posn (newE 'Posn (list (numE 2) (numE 7))))
        (objV 'Posn (list (numV 2) (numV 7))))

  (test (interp-posn (sendE posn27 'mdist (numE 0)))
        (numV 9))
  
  (test (interp-posn (sendE posn27 'addX (numE 10)))
        (numV 12))

  (test (interp-posn (sendE (ssendE posn27 'Posn 'factory12 (numE 0))
                            'multY
                            (numE 15)))
        (numV 30))

  (test (interp-posn (sendE posn531 'addDist posn27))
        (numV 18))

  (test/exn (interp (idE 'x)
                empty (objV 'Object empty) (numV 0) mt-env)
        "no such binding")
  (test/exn (interp (arrayrefE (newarrayE 'num (numE 5) (numE 10)) (numE 5))
                empty (objV 'Object empty) (numV 0) mt-env)
        "Given index larger than array size.")
  (test/exn (interp (arraysetE (newarrayE 'num (numE 5) (numE 10)) (numE 5) (numE 32)) ;; implement the let form, will give final stars and enable test
                empty (objV 'Object empty) (numV 0) mt-env)
        "Given index larger than array size.")
  (test/exn (interp-posn (plusE (numE 1) posn27))
            "not a number")
  (test/exn (interp-posn (getE (numE 1) 'x))
            "not an object")
  (test/exn (interp-posn (sendE (numE 1) 'mdist (numE 0)))
            "not an object")
  (test/exn (interp-posn (ssendE (numE 1) 'Posn 'mdist (numE 0)))
            "not an object")
  (test/exn (interp-posn (newE 'Posn (list (numE 0))))
            "wrong field count")
  (test/exn (interp (getE (nulE) 'z)
                (list null-class) (numV -1) (numV -1) mt-env)
        "")
  (test/exn (interp (sendE (nulE) 'test (nulE))
                (list null-class) (numV -1) (numV -1) mt-env)
        ""))