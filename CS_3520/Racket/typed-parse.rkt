#lang plait
(require "class.rkt"
         "inherit.rkt"
         "typed-class.rkt"
         "inherit-parse.rkt")

(module+ test
  (print-only-errors #t))

;; ----------------------------------------

(define (parse-t-class [s : S-Exp]) : (Symbol * ClassT)
  (cond
    [(s-exp-match? `{class SYMBOL extends SYMBOL {ANY ...} ANY ...} s)
     (values
      (s-exp->symbol (second (s-exp->list s)))
      (classT (s-exp->symbol (fourth (s-exp->list s)))
              (map parse-t-field
                   (s-exp->list (fourth (rest (s-exp->list s)))))
              (map parse-t-method 
                   (rest (rest (rest (rest (rest (s-exp->list s)))))))))]
    [else (error 'parse-t-class "invalid input")]))

(define (parse-t-field [s : S-Exp]) : (Symbol * Type)
  (cond
    [(s-exp-match? `[SYMBOL : ANY] s)
     (values (s-exp->symbol (first (s-exp->list s)))
             (parse-type (third (s-exp->list s))))]
    [else (error 'parse-t-field "invalid input")]))

(define (parse-t-method [s : S-Exp]) : (Symbol * MethodT)
  (cond
    [(s-exp-match? `[SYMBOL {[arg : ANY]} : ANY ANY] s)
     (values
      (s-exp->symbol (first (s-exp->list s)))
      (methodT (parse-type (local [(define args (second (s-exp->list s)))
                                   (define arg (first (s-exp->list args)))]
                             (third (s-exp->list arg))))
               (parse-type (fourth (s-exp->list s)))
               (parse (fourth (rest (s-exp->list s))))))]
    [else (error 'parse-t-method "invalid input")]))

(define (parse-type [s : S-Exp]) : Type
  (cond
    [(s-exp-match? `num s)
     (numT)]
    [(s-exp-match? `null s)
     (nulT)]
    [(s-exp-match? `array-of-num s)
     (arrayT (numT))]
    [(s-exp-match? `array-of-object s)
     (arrayT (objT 'Object))]
    [(s-exp-match? `array-of-null s)
     (arrayT (nulT))]
    [(s-exp-match? `array-of-array-of-num s)
     (arrayT (arrayT (numT)))]
    [(s-exp-match? `array-of-array-of-object s)
     (arrayT (arrayT (objT 'Object)))]
    [(s-exp-match? `SYMBOL s)
     (objT (s-exp->symbol s))]
    [else (error 'parse-type "invalid input")]))

(module+ test
  (test (parse-type `num)
        (numT))
  (test (parse-type `Object)
        (objT 'Object))
  (test (parse-type `null)
        (nulT))
  (test (parse-type `array-of-null)
        (arrayT (nulT)))
  (test (parse-type `array-of-num)
        (arrayT (numT)))
  (test (parse-type `array-of-object)
        (arrayT (objT 'Object)))
  (test (parse-type `array-of-array-of-num)
        (arrayT (arrayT (numT))))
  (test (parse-type `array-of-array-of-object)
        (arrayT (arrayT (objT 'Object))))
  (test/exn (parse-type `{})
            "invalid input")
  
  (test (parse-t-field `[x : num])
        (values 'x (numT)))
  (test/exn (parse-t-field `{x 1})
            "invalid input")

  (test (parse-t-method `[m {[arg : num]} : Object this])
        (values 'm (methodT (numT) (objT 'Object) (thisI))))
  (test/exn (parse-t-method `{m 1})
            "invalid input")
  
  (test (parse-t-class `{class Posn3D extends Posn
                          {[x : num] [y : num]}
                          [m1 {[arg : num]} : num arg]
                          [m2 ([arg : num]) : Object this]})
        (values 'Posn3D
                (classT 'Posn
                        (list (values 'x (numT))
                              (values 'y (numT)))
                        (list (values 'm1 (methodT (numT) (numT) (argI)))
                              (values 'm2 (methodT (numT) (objT 'Object) (thisI)))))))
  (test/exn (parse-t-class `{class})
            "invalid input"))

;; ----------------------------------------

(define (interp-t-prog [classes : (Listof S-Exp)] [a : S-Exp]) : S-Exp
  (let ([v (interp-t (parse a)
                     (map parse-t-class classes))])
    (type-case Value v
      [(numV n) (number->s-exp n)]
      [(objV class-name field-vals) `object]
      [(nulV) `object]
      [(arrayV type list) `array])))

(module+ test
  (test (interp-t-prog
         (list
          `{class Empty extends Object
             {}})
         `{new Empty})
        `object)

  (test (interp-t-prog
         empty
         `null)
        `object)

  (test (interp-t-prog
         empty
         `{if0 {+ 0 0} {+ 5 8} {* 2 10}})
        `13)

  (test (interp-t-prog
         empty
         `{newarray num 5 10})
        `array)

  (test (interp-t-prog
         empty
         `{newarray object 5 null})
        `array)

  (test (interp-t-prog
         empty
         `{if0 {+ 0 1} {+ 5 8} {* 2 10}})
        `20)

  (test (interp-t-prog
         (list
          `{class Test extends Object
             {[x : array-of-num]
              [y : array-of-object]}
             [getelem {[arg : num]} : num
                  {arrayref {get this x} arg}]})
         `{new Test {newarray num 5 10} {newarray object 5 null}})
        `object)

  (test (interp-t-prog
         (list
          `{class Test extends Object
             {[x : array-of-num]
              [y : array-of-object]}
             [getelem {[arg : num]} : num
                  {arrayref {get this x} arg}]})
         `{let {[obj {new Test {newarray num 5 10} {newarray object 5 null}}]} obj})
        `object)

  (test (interp-t-prog
         (list
          `{class Test extends Object
             {[x : array-of-num]
              [y : array-of-object]}
             [getnumelem {[arg : num]} : num
                  {arrayref {get this x} arg}]
             [getobjelem {[arg : num]} : Object
                         {arrayref {get this y} arg}]})
         `{send {new Test {newarray num 5 10} {newarray object 5 null}} getnumelem 4})
        `10)

  (test (interp-t-prog
         (list
          `{class Test extends Object
             {[x : array-of-num]
              [y : array-of-object]}
             [getnumelem {[arg : num]} : num
                  {arrayref {get this x} arg}]
             [getobjelem {[arg : num]} : Object
                         {arrayref {get this y} arg}]})
         `{send {new Test {newarray num 5 10} {newarray object 5 null}} getobjelem 4})
        `object)

  (test (interp-t-prog
         (list
          `{class Test extends Object
             {[x : array-of-num]
              [y : array-of-object]}
             [getnumelem {[arg : num]} : num
                  {arrayref {get this x} arg}]
             [getobjelem {[arg : num]} : Object
                         {arrayref {get this y} arg}]})
         `{let {[obj {new Test {newarray num 5 10} {newarray object 5 null}}]} {if0 {arrayset {get obj x} 4 55} {send obj getnumelem 4} -1}})
        `55) ;;Another Imperative test

 (test (interp-t-prog 
        (list
         `{class Posn extends Object
            {[x : num]
             [y : num]}
            [mdist {[arg : num]} : num
                   {+ {get this x} {get this y}}]
            [addDist {[arg : Posn]} : num
                     {+ {send arg mdist 0}
                        {send this mdist 0}}]}
         
         `{class Posn3D extends Posn
            {[z : num]}
            [mdist {[arg : num]} : num
                   {+ {get this z} 
                      {super mdist arg}}]})
        
        `{send {new Posn3D 5 3 1} addDist {new Posn 2 7}})
       `18)

  (test (interp-t-prog 
        (list
         `{class Posn extends Object
            {[x : num]
             [y : num]}
            [mdist {[arg : num]} : num
                   {+ {get this x} {get this y}}]
            [addDist {[arg : Posn]} : num
                     {send this mdist 0}]}
         
         `{class Posn3D extends Posn
            {[z : num]}
            [mdist {[arg : num]} : num
                   {+ {get this z} 
                      {super mdist arg}}]})
        
        `{send {new Posn3D 5 3 1} addDist null})
       `9)

  (test (interp-t-prog 
        (list
         `{class Posn extends Object
            {[x : num]
             [y : num]}
            [mdist {[arg : num]} : num
                   {+ {get this x} {get this y}}]
            [addDist {[arg : Posn]} : num
                     {+ {send arg mdist 0}
                        {send this mdist 0}}]}
         
         `{class Posn3D extends Posn
            {[z : Posn]}
            [mdist {[arg : num]} : num
                   {super mdist arg}]})
        
        `{send {new Posn3D 5 3 null} addDist {new Posn 2 7}})
       `17)

  (test (interp-t-prog 
        (list
         `{class Posn extends Object
            {[x : num]
             [y : num]}
            [mdist {[arg : num]} : num
                   {+ {get this x} {get this y}}]
            [addDist {[arg : Posn]} : num
                     {+ {send arg mdist 0}
                        {send this mdist 0}}]}
         
         `{class Posn3D extends Posn
            {[z : Posn]}
            [mdist {[arg : num]} : num
                   {super mdist arg}]})
        
        `{if0 0 {new Posn3D 5 2 1} {new Posn 2 1}})
       `object)

  (test (interp-t-prog 
        (list
         `{class Posn extends Object
            {[x : num]
             [y : num]}
            [mdist {[arg : num]} : num
                   {+ {get this x} {get this y}}]
            [addDist {[arg : Posn]} : num
                     {+ {send arg mdist 0}
                        {send this mdist 0}}]}
         
         `{class Posn3D extends Posn
            {[z : num]}
            [mdist {[arg : num]} : num
                   {+ {get this z} 
                      {super mdist arg}}]})
        
        `{send {if0 0 {new Posn3D 5 2 1} {new Posn 2 1}} addDist {new Posn 2 7}})
       `17)

  (test/exn (interp-t-prog 
        (list
         `{class Posn extends Object
            {[x : num]
             [y : num]}
            [mdist {[arg : num]} : num
                   {+ {get this x} {get this y}}]
            [addDist {[arg : Posn]} : num
                     {+ {send arg mdist 0}
                        {send this mdist 0}}]}
         
         `{class Posn3D extends Posn
            {[z : num]}
            [mdist {[arg : num]} : num
                   {+ {get this z} 
                      {super mdist arg}}]})
        
        `{send {new Posn3D 5 3 null} addDist {new Posn 2 7}})
       "interp: not a number")

  (test/exn (interp-t-prog 
        (list
         `{class Posn extends Object
            {[x : num]
             [y : num]}
            [mdist {[arg : num]} : num
                   {+ {get this x} {get this y}}]
            [addDist {[arg : Posn]} : num
                     {+ {send arg mdist 0}
                        {send this mdist 0}}]}
         
         `{class Posn3D extends Posn
            {[z : num]}
            [mdist {[arg : num]} : num
                   {+ {get this z} 
                      {super mdist arg}}]})
        
        `{send {new Posn3D 5 3 1} addDist null})
       "interp: not an object"))