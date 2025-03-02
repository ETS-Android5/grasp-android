(import (srfi :11))
(import (srfi :17))
(import (define-interface))
(import (define-type))
(import (define-object))
(import (define-property))
(import (match))
(import (infix))
(import (assert))
(import (for))
(import (examples))
(import (define-cache))
(import (print))
(import (string-building))
(import (functions))


(define-alias Index java.lang.Object)

(define-alias Indexable* java.lang.Object)

;; we consider the Null class to be a class whose
;; only member #!null
(define-alias Null java.lang.Object)

(define-interface Indexable ()
  (part-at index::Index)::Indexable*
  
  (first-index)::Index
  (last-index)::Index
  
  (next-index index::Index)::Index
  (previous-index index::Index)::Index

  (index< a::Index b::Index)::boolean

)



