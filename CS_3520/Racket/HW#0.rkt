#lang plait

(define (3rd-power [n : Number]) : Number
  (*(* n n) n))

(test (3rd-power -3) -27)

(test (3rd-power 0) 0)

(test (3rd-power -1) -1)

(test (3rd-power 17) 4913)

(define (42nd-power [n : Number]) : Number
  (* (* (* (3rd-power (3rd-power (3rd-power n)))
        (3rd-power (3rd-power n))) (3rd-power n)) (3rd-power n)))

(test (42nd-power 3) 109418989131512359209)

(test (42nd-power 0) 0)

(test (42nd-power -1) 1)

(test (42nd-power 17)
      4773695331839566234818968439734627784374274207965089)

(define (plural [s : String]) : String
  (cond
    [(string=? s "") "s"]
    [(string=? (substring s (- (string-length s) 1)
    (string-length s)) "y") (string-append
    (substring s 0 (- (string-length s) 1)) "ies")]
    [else (string-append s "s")]))

(test (plural "baby") "babies")

(test (plural "fish") "fishs")

(test (plural "") "")

(test (plural "wlkejkalsdifey") "wlkejkalsdifeies")

(define-type Light
    (bulb [watts : Number]
          [technology : Symbol])
    (candle [inches : Number]))

(define (energy-usage [l : Light]) : Number
  (type-case Light l
  [(bulb w t) (/ (* w 24) 1000)]
  [(candle i) 0]))

(test (energy-usage (bulb 100.0 'halogen)) 2.4)

(test (energy-usage (candle 10)) 0)

(test (energy-usage (bulb 150 'fluorescent)) 3.6)

(define (use-for-one-hour [l : Light]) : Light
  (type-case Light l
    [(bulb w t) l]
    [(candle i) (cond [(= i 0) (candle 0.0)]
                      [else (candle (- i 1))])]
    ))

(test (use-for-one-hour (bulb 100.0 'halogen)) (bulb 100.0 'halogen))

(test (use-for-one-hour (bulb 150.0 'fluorescent))
      (bulb 150.0 'fluorescent))

(test (use-for-one-hour (candle 100)) (candle 99))

(test (use-for-one-hour (candle 0)) (candle 0.0))