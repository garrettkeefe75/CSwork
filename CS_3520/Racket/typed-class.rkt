#lang plait

(require "class.rkt"
         "inherit.rkt")

(define-type ClassT
  (classT [super-name : Symbol]
          [fields : (Listof (Symbol * Type))]
          [methods : (Listof (Symbol * MethodT))]))

(define-type MethodT
  (methodT [arg-type : Type]
           [result-type : Type]
           [body-expr : ExpI]))

(define-type Type
  (numT)
  (objT [class-name : Symbol])
  (nulT)
  (arrayT [subtype : Type]))

(define-type Type-Binding
  (tbind [name : Symbol]
         [type : Type]))

(define-type-alias Type-Env (Listof Type-Binding))


(module+ test
  (print-only-errors #t))

;; ----------------------------------------

(define (type-error a msg)
  (error 'typecheck (string-append
                     "no type: "
                     (string-append
                      (to-string a)
                      (string-append " not "
                                     msg)))))

(define (get-all-field-types class-name t-classes)
  (if (equal? class-name 'Object)
      empty        
      (type-case ClassT (find t-classes class-name)
        [(classT super-name fields methods)
         (append 
          (get-all-field-types super-name t-classes)
          (map snd fields))])))

(define (parse-symbol [s : Symbol]) : Type
  (cond
    [(equal? 'num s)
     (numT)]
    [(equal? 'null s)
     (nulT)]
    [(equal? 'array-of-num s)
     (arrayT (numT))]
    [(equal? 'array-of-object s)
     (arrayT (objT 'Object))]
    [(equal? 'object s)
     (objT 'Object)]
    [else (error 'parse-type "invalid input")]))

;; ----------------------------------------

(define (make-find-in-tree class-items)
  (lambda (name class-name t-classes)
    (local [(define t-class (find t-classes class-name))
            (define items (class-items t-class))
            (define super-name 
              (classT-super-name t-class))]
      (if (equal? super-name 'Object)
          (find items name)
          (try (find items name)
               (lambda ()
                 ((make-find-in-tree class-items)
                  name 
                  super-name
                  t-classes)))))))

(define find-field-in-tree
  (make-find-in-tree classT-fields))

(define find-method-in-tree
  (make-find-in-tree classT-methods))

;; ----------------------------------------

(define (is-subclass? name1 name2 t-classes)
  (cond
    [(equal? name1 name2) #t]
    [(equal? name1 'Object) #f]
    [else
     (type-case ClassT (find t-classes name1)
       [(classT super-name fields methods)
        (is-subclass? super-name name2 t-classes)])]))

(define (is-subtype? t1 t2 t-classes)
  (type-case Type t1
    [(objT name1)
     (type-case Type t2 
       [(objT name2)
        (is-subclass? name1 name2 t-classes)]
       [(nulT) #t]
       [else #f])]
    [(nulT)
     (type-case Type t2
       [(objT name2) #t]
       [(nulT) #t]
       [else #f])]
    [(arrayT subtype1)
     (type-case Type t2
       [(arrayT subtype2)
        (if (is-subtype? subtype1 subtype2 t-classes)
            #t
            #f)]
       [else #f])]
    [else (equal? t1 t2)]))

(module+ test
  (define a-t-class (values 'A (classT 'Object empty empty)))
  (define b-t-class (values 'B (classT 'A empty empty)))

  (test (is-subclass? 'Object 'Object empty)
        #t)
  (test (is-subclass? 'A 'B (list a-t-class b-t-class))
        #f)
  (test (is-subclass? 'B 'A (list a-t-class b-t-class))
        #t)

  (test (is-subtype? (numT) (numT) empty)
        #t)
  (test (is-subtype? (nulT) (objT 'Object) empty)
        #t)
  (test (is-subtype? (nulT) (nulT) empty)
        #t)
  (test (is-subtype? (objT 'Object) (nulT) empty)
        #t)
  (test (is-subtype? (numT) (objT 'Object) empty)
        #f)
  (test (is-subtype? (objT 'Object) (numT) empty)
        #f)
  (test (is-subtype? (objT 'A) (objT 'B) (list a-t-class b-t-class))
        #f)
  (test (is-subtype? (objT 'B) (objT 'A) (list a-t-class b-t-class))
        #t)
  (test (is-subtype? (arrayT (numT)) (arrayT (numT)) empty)
        #t)
  (test (is-subtype? (arrayT (numT)) (arrayT (nulT)) empty)
        #f)
  (test (is-subtype? (arrayT (arrayT (numT))) (arrayT (numT)) empty)
        #f))

;; ----------------------------------------

(define typecheck-expr : (ExpI (Listof (Symbol * ClassT)) Type Type Type-Env -> Type)
  (lambda (expr t-classes this-type arg-type tenv)
    (local [(define (recur expr)
              (typecheck-expr expr t-classes this-type arg-type tenv))
            (define (typecheck-nums l r)
              (type-case Type (recur l)
                [(numT)
                 (type-case Type (recur r)
                   [(numT) (numT)]
                   [else (type-error r "num")])]
                [else (type-error l "num")]))]
      (type-case ExpI expr
        [(numI n) (numT)]
        [(nulI) (nulT)]
        [(plusI l r) (typecheck-nums l r)]
        [(multI l r) (typecheck-nums l r)]
        [(argI) arg-type]
        [(thisI) this-type]
        [(idI s) (type-lookup s tenv)]
        [(newI class-name exprs)
         (local [(define arg-types (map recur exprs))
                 (define field-types
                   (get-all-field-types class-name t-classes))]
           (if (and (= (length arg-types) (length field-types))
                    (foldl (lambda (b r) (and r b))
                           #t
                           (map2 (lambda (t1 t2) 
                                   (is-subtype? t1 t2 t-classes))
                                 arg-types
                                 field-types)))
               (objT class-name)
               (type-error expr "field type mismatch")))]
        [(getI obj-expr field-name)
         (type-case Type (recur obj-expr)
           [(objT class-name)
            (find-field-in-tree field-name
                                class-name
                                t-classes)]
           [else (type-error obj-expr "object")])]
        [(sendI obj-expr method-name arg-expr)
         (local [(define obj-type (recur obj-expr))
                 (define arg-type (recur arg-expr))]
           (type-case Type obj-type
             [(objT class-name)
              (typecheck-send class-name method-name
                              arg-expr arg-type
                              t-classes)]
             [else
              (type-error obj-expr "object")]))]
        [(superI method-name arg-expr)
         (local [(define arg-type (recur arg-expr))
                 (define this-class
                   (find t-classes (objT-class-name this-type)))]
           (typecheck-send (classT-super-name this-class)
                           method-name
                           arg-expr arg-type
                           t-classes))]
        [(letI name val body)
         (typecheck-expr body t-classes this-type arg-type (extend-env (tbind name (recur val)) tenv))]
        [(if0I test-expr then-expr else-expr)
         (type-case Type (recur test-expr)
           [(numT)
            (local [(define then-type (recur then-expr))
                    (define else-type (recur else-expr))]
              (type-case Type then-type
                [(nulT)
                 (type-case Type else-type
                   [(objT class-name) else-type]
                   [(nulT) (nulT)]
                   [else (type-error else-expr "object")])]
                [(objT class-name1)
                 (type-case Type else-type
                   [(nulT) then-type]
                   [(objT class-name2) (least-upper-bound class-name1 class-name2 t-classes)]
                   [else (type-error else-expr "object")])]
                [(numT)
                 (type-case Type else-type
                   [(numT) (numT)]
                   [else (type-error else-expr "number")])]
                [(arrayT subtype1)
                 (type-case Type else-type
                   [(arrayT subtype2)
                    (if (is-subtype? subtype1 subtype2 t-classes)
                        (arrayT subtype1)
                        (type-error subtype2 (to-string subtype1)))]
                   [else (type-error else-expr "array")])]))]
           [else (type-error test-expr "number")])]
        [(newarrayI type size elem)
         (local [(define array-of-type (parse-symbol type))
                 (define size-type (recur size))
                 (define elem-type (recur elem))]
           (type-case Type size-type
             [(numT)
              (cond
                [(is-subtype? elem-type array-of-type t-classes) (arrayT array-of-type)]
                [else (type-error elem (to-string array-of-type))])]
             [else (type-error size "number")]))]
        [(arrayrefI array index)
         (type-case Type (recur index)
           [(numT)
            (type-case Type (recur array)
              [(arrayT subtype) subtype]
              [else (type-error array "array")])]
           [else (type-error index "number")])]
        [(arraysetI array index elem)
         (type-case Type (recur index)
           [(numT)
            (type-case Type (recur array)
              [(arrayT subtype)
               (local [(define elem-type (recur elem))]
                 (if (is-subtype? elem-type subtype t-classes)
                     (numT)
                     (type-error elem (to-string subtype))))]
              [else (type-error array "array")])]
           [else (type-error index "number")])]))))

(define (least-upper-bound [name1 : Symbol] [name2 : Symbol] [t-classes : (Listof (Symbol * ClassT))]) : Type
  (local [(define names (cons name2 (class->list-supers name2 t-classes)))]
    (if (found-name? name1 names)
        (objT name1)
        (least-upper-bound-helper name1 names t-classes))))

(define (least-upper-bound-helper [name : Symbol] [names : (Listof Symbol)] [t-classes : (Listof (Symbol * ClassT))])
  (local [(define super-name (classT-super-name (find t-classes name)))]
    (if (found-name? super-name names)
        (objT super-name)
        (least-upper-bound-helper super-name names t-classes))))

(define (found-name? [name : Symbol] [names : (Listof Symbol)])
  (cond
    [(empty? names) #f]
    [(equal? (first names) name) #t]
    [else (found-name? name (rest names))]))

(define (class->list-supers [name : Symbol] [t-classes : (Listof (Symbol * ClassT))]) : (Listof Symbol)
  (if (equal? 'Object name)
      (cons 'Object empty)
      (local [(define super-name (classT-super-name (find t-classes name)))]
        (if (equal? super-name 'Object)
            (cons 'Object empty)
            (cons super-name (class->list-supers super-name t-classes))))))

(define (type-lookup [name : Symbol] [tenv : Type-Env])
  (cond
    [(empty? tenv) (error 'type-lookup "no such type binding")]
    [(equal? name (tbind-name (first tenv)))(tbind-type (first tenv))]
    [else (type-lookup name (rest tenv))]))


(define (typecheck-send [class-name : Symbol]
                        [method-name : Symbol]
                        [arg-expr : ExpI]
                        [arg-type : Type]
                        [t-classes : (Listof (Symbol * ClassT))])
  (type-case MethodT (find-method-in-tree
                      method-name
                      class-name
                      t-classes)
    [(methodT arg-type-m result-type body-expr)
     (if (is-subtype? arg-type arg-type-m t-classes)
         result-type
         (type-error arg-expr (to-string arg-type-m)))]))

(define (typecheck-method [method : MethodT]
                          [this-type : Type]
                          [t-classes : (Listof (Symbol * ClassT))]
                          [tenv : Type-Env]) : ()
  (type-case MethodT method
    [(methodT arg-type result-type body-expr)
     (if (is-subtype? (typecheck-expr body-expr t-classes
                                      this-type arg-type tenv)
                      result-type
                      t-classes)
         (values)
         (type-error body-expr (to-string result-type)))]))

(define (check-override [method-name : Symbol]
                        [method : MethodT]
                        [this-class : ClassT]
                        [t-classes : (Listof (Symbol * ClassT))])
  (local [(define super-name 
            (classT-super-name this-class))
          (define super-method
            (try
             ;; Look for method in superclass:
             (find-method-in-tree method-name
                                  super-name
                                  t-classes)
             ;; no such method in superclass:
             (lambda () method)))]
    (if (and (equal? (methodT-arg-type method)
                     (methodT-arg-type super-method))
             (equal? (methodT-result-type method)
                     (methodT-result-type super-method)))
        (values)
        (error 'typecheck (string-append
                           "bad override of "
                           (to-string method-name))))))

(define (typecheck-class [class-name : Symbol]
                         [t-class : ClassT]
                         [t-classes : (Listof (Symbol * ClassT))]
                         [tenv : Type-Env])
  (type-case ClassT t-class
    [(classT super-name fields methods)
     (map (lambda (m)
            (begin
              (typecheck-method (snd m) (objT class-name) t-classes tenv)
              (check-override (fst m) (snd m) t-class t-classes)))
          methods)]))

(define (typecheck [a : ExpI]
                   [t-classes : (Listof (Symbol * ClassT))]) : Type
  (begin
    (map (lambda (tc)
           (typecheck-class (fst tc) (snd tc) t-classes mt-env))
         t-classes)
    (typecheck-expr a t-classes (objT 'Object) (numT) mt-env)))

;; ----------------------------------------

(module+ test
  (define posn-t-class
    (values 'Posn
            (classT 'Object
                    (list (values 'x (numT)) (values 'y (numT)))
                    (list (values 'mdist
                                  (methodT (numT) (numT) 
                                           (plusI (getI (thisI) 'x) (getI (thisI) 'y))))
                          (values 'addDist
                                  (methodT (objT 'Posn) (numT)
                                           (plusI (sendI (thisI) 'mdist (numI 0))
                                                  (sendI (argI) 'mdist (numI 0)))))))))

  (define posn3D-t-class 
    (values 'Posn3D
            (classT 'Posn
                    (list (values 'z (numT)))
                    (list (values 'mdist
                                  (methodT (numT) (numT)
                                           (plusI (getI (thisI) 'z) 
                                                  (superI 'mdist (argI)))))))))

  (define square-t-class 
    (values 'Square
            (classT 'Object
                    (list (values 'topleft (objT 'Posn)))
                    (list))))

  (define null-t-class
    (values 'NullClass
            (classT 'Object
                    (list (values 'z (objT 'Object)))
                    (list (values 'test
                                  (methodT (objT 'Object) (objT 'Object)
                                           (getI (thisI) 'z)))))))

  (define (typecheck-posn a)
    (typecheck a
               (list posn-t-class posn3D-t-class square-t-class)))
  
  (define new-posn27 (newI 'Posn (list (numI 2) (numI 7))))
  (define new-posn531 (newI 'Posn3D (list (numI 5) (numI 3) (numI 1))))
  (define new-Null (newI 'NullClass (list (nulI))))

  (test (typecheck new-Null
                   (list null-t-class))
        (objT 'NullClass))

  (test (typecheck (sendI new-Null 'test (nulI))
                   (list null-t-class))
        (objT 'Object))

  (test (typecheck (letI 'y (numI 100) (letI 'x (numI 50) (plusI (idI 'y) (idI 'x))))
                   empty)
        (numT))

  (test (typecheck-posn (sendI new-posn27 'mdist (numI 0)))
        (numT))
  (test (typecheck-posn (sendI new-posn531 'mdist (numI 0)))
        (numT))  
  (test (typecheck-posn (sendI new-posn531 'addDist new-posn27))
        (numT))  
  (test (typecheck-posn (sendI new-posn27 'addDist new-posn531))
        (numT))
  (test (typecheck-posn (if0I (numI 0) new-posn27 new-posn531))
        (objT 'Posn))
  (test (typecheck-posn (if0I (numI 0) new-posn531 new-posn531))
        (objT 'Posn3D))
  (test (typecheck-posn (if0I (numI 0) (newI 'Object empty) new-posn531))
        (objT 'Object))
  (test (typecheck-posn (if0I (numI 0) new-posn531 (newI 'Object empty)))
        (objT 'Object))
  (test (typecheck-posn (if0I (numI 0) new-posn531 (newI 'Square (list (nulI)))))
        (objT 'Object))

  (test (typecheck-posn (newI 'Square (list (newI 'Posn (list (numI 0) (numI 1))))))
        (objT 'Square))
  (test (typecheck-posn (newI 'Square (list (nulI))))
        (objT 'Square))
  (test (typecheck-posn (newI 'Square (list (newI 'Posn3D (list (numI 0) (numI 1) (numI 3))))))
        (objT 'Square))
  (test (typecheck-posn (if0I (numI 0) (newI 'Square (list (nulI))) (nulI)))
            (objT 'Square))
  (test (typecheck-posn (if0I (numI 0) (nulI) (newI 'Square (list (newI 'Posn3D (list (numI 0) (numI 1) (numI 3)))))))
            (objT 'Square))

  (test (typecheck (if0I (numI 0) (nulI) (nulI))
                       empty)
            (nulT))

  (test (typecheck (if0I (numI 0) (numI 12) (numI 22))
                       empty)
            (numT))
  
  (test (typecheck (multI (numI 1) (numI 2))
                   empty)
        (numT))
  (test (typecheck (nulI)
                   empty)
        (nulT))

  (test (typecheck (newarrayI 'num (numI 5) (numI 10))
                   empty)
        (arrayT (numT)))
  (test (typecheck (newarrayI 'object (numI 5) new-Null)
                   (list null-t-class))
        (arrayT (objT 'Object)))
  (test (typecheck (newarrayI 'object (numI 5) (nulI))
                   empty)
        (arrayT (objT 'Object)))
  (test (typecheck (newarrayI 'null (numI 5) (nulI))
                   empty)
        (arrayT (nulT)))
  (test (typecheck (newarrayI 'array-of-num (numI 5) (newarrayI 'num (numI 5) (numI 10)))
                   empty)
        (arrayT (arrayT (numT))))
  (test (typecheck (newarrayI 'array-of-object (numI 5) (newarrayI 'object (numI 5) (nulI)))
                   empty)
        (arrayT (arrayT (objT 'Object))))
  (test (typecheck (arrayrefI (newarrayI 'num (numI 5) (numI 10)) (numI 2))
                   empty)
        (numT))
  (test (typecheck (arraysetI (newarrayI 'object (numI 5) (nulI)) (numI 2) new-Null)
                   (list null-t-class))
        (numT))
  (test (typecheck (if0I (numI 0) (newarrayI 'num (numI 5) (numI 10)) (newarrayI 'num (numI 5) (numI 10)))
                   empty)
        (arrayT (numT)))
  (test (typecheck (if0I (numI 0) (newarrayI 'object (numI 5) (nulI)) (newarrayI 'object (numI 5) new-Null))
                   (list null-t-class))
        (arrayT (objT 'Object)))

  (test/exn (typecheck (idI 'x)
            empty)
            "no such type binding")
  (test/exn (typecheck (arrayrefI (nulI) (numI 2))
                   empty)
        "no type")
  (test/exn (typecheck (arrayrefI (newarrayI 'num (numI 5) (numI 10)) (nulI))
                   empty)
        "no type")
  (test/exn (typecheck (arraysetI (nulI) (numI 2) (numI 32))
                   empty)
        "no type")
  (test/exn (typecheck (arraysetI (newarrayI 'num (numI 5) (numI 10)) (nulI) (numI 32))
                   empty)
        "no type")
  (test/exn (typecheck (arraysetI (newarrayI 'num (numI 5) (numI 10)) (numI 2) new-Null)
                   (list null-t-class))
        "no type")
  (test/exn (typecheck (newarrayI 'nonsense (numI 5) (nulI))
                   empty)
        "invalid input")
  (test/exn (typecheck (newarrayI 'num (numI 5) new-Null)
                   (list null-t-class))
        "no type")
  (test/exn (typecheck (newarrayI 'array-of-num (numI 5) (newarrayI 'object (numI 5) (nulI)))
                   empty)
        "no type")
  (test/exn (typecheck (newarrayI 'num (nulI) (numI 10))
                   empty)
        "no type")
  (test/exn (typecheck (if0I (numI 0) (newarrayI 'num (numI 5) (numI 10)) (newarrayI 'object (numI 5) new-Null))
                   (list null-t-class))
        "no type")
  (test/exn (typecheck (if0I (numI 0) (newarrayI 'num (numI 5) (numI 10)) new-Null)
                   (list null-t-class))
        "no type")

  (test/exn (typecheck-posn (sendI (numI 10) 'mdist (numI 0)))
            "no type")
  (test/exn (typecheck-posn (sendI new-posn27 'mdist new-posn27))
            "no type")
  (test/exn (typecheck-posn (sendI new-posn27 'mdist (nulI)))
            "no type")
  (test/exn (typecheck-posn (newI 'Square (list (newI 'Posn (list (nulI) (nulI))))))
        "no type")
  (test/exn (typecheck (plusI (numI 1) (newI 'Object empty))
                       empty)
            "no type")

  (test/exn (typecheck (if0I (nulI) (plusI (numI 1) (numI 2)) (plusI (numI 0) (numI 3)))
                       empty)
            "no type")

  (test/exn (typecheck (if0I (numI 0) (plusI (numI 1) (numI 2)) (newI 'Object empty))
                       empty)
            "no type")

  (test/exn (typecheck (if0I (numI 0) (newI 'Object empty) (plusI (numI 1) (numI 2)))
                       empty)
            "no type")

  (test/exn (typecheck (if0I (numI 0) (nulI) (plusI (numI 1) (numI 2)))
                       empty)
            "no type")
  
  (test/exn (typecheck (plusI (newI 'Object empty) (numI 1))
                       empty)
            "no type")
  (test/exn (typecheck (plusI (numI 1) (newI 'Object (list (numI 1))))
                       empty)
            "no type")
  (test/exn (typecheck (getI (numI 1) 'x)
                       empty)
            "no type")
  (test/exn (typecheck (numI 10)
                       (list posn-t-class
                             (values 'Other
                                     (classT 'Posn
                                             (list)
                                             (list (values 'mdist
                                                           (methodT (objT 'Object) (numT)
                                                                    (numI 10))))))))
            "bad override")
  (test/exn (typecheck-method (methodT (numT) (objT 'Object) (numI 0)) (objT 'Object) empty mt-env)
            "no type")
  (test/exn (typecheck (numI 0)
                       (list square-t-class
                             (values 'Cube
                                     (classT 'Square
                                             empty
                                             (list
                                              (values 'm
                                                      (methodT (numT) (numT)
                                                               ;; No such method in superclass:
                                                               (superI 'm (numI 0)))))))))
            "not found")
  (test/exn (typecheck (multI (nulI) (numI 10))
                       empty)
            "no type")
  
  (test/exn (typecheck (getI (nulI) 'z)
                   (list null-t-class))
        "no type")

  (test/exn (typecheck (sendI (nulI) 'test (nulI))
                       (list null-t-class))
            "no type"))

;; ----------------------------------------

(define strip-types : (ClassT -> ClassI)
  (lambda (t-class)
    (type-case ClassT t-class
      [(classT super-name fields methods)
       (classI
        super-name
        (map fst fields)
        (map (lambda (m)
               (values (fst m)
                       (type-case MethodT (snd m)
                         [(methodT arg-type result-type body-expr)
                          body-expr])))
             methods))])))
  
(define interp-t : (ExpI (Listof (Symbol * ClassT)) -> Value)
  (lambda (a t-classes)
    (interp-i a
              (map (lambda (c)
                     (values (fst c) (strip-types (snd c))))
                   t-classes))))

(module+ test
  (define (interp-t-posn a)
    (interp-t a
              (list posn-t-class posn3D-t-class)))
  
  (test (interp-t-posn (sendI new-posn27 'mdist (numI 0)))
        (numV 9))  
  (test (interp-t-posn (sendI new-posn531 'mdist (numI 0)))
        (numV 9))
  (test (interp-t-posn (sendI new-posn531 'addDist new-posn27))
        (numV 18))
  (test (interp-t-posn (sendI new-posn27 'addDist new-posn531))
        (numV 18)))