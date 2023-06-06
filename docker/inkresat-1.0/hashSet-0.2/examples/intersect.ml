(* example adapted from
 * http://caml.inria.fr/pub/ml-archives/caml-list/2002/08/e0d4289c38737e87235cbe9eccb7dbf8.en.html 
 *)
open HashSet;;

let screate nitems maxval =
(* create dictionary of nitems with integer keys from (0,maxval) interval *)
  let s = create 0 0 and
  size = ref 0 and
  rnd = ref 0 in
  while !size < nitems && !size < maxval do
    rnd := Random.int maxval;
    add s !rnd;
    size := length s;
  done;
s;;  

let nitems = if Array.length Sys.argv > 1 then int_of_string Sys.argv.(1) else 300000 and
maxval = if Array.length Sys.argv > 2 then int_of_string Sys.argv.(2) else 500000;;
Printf.printf "intersecting sets of %d items from (0,%d) ... " nitems maxval;
flush stdout;
let s1 = screate nitems maxval and
s2 = screate nitems maxval in
let t1 = Sys.time () in
inter_update s1 s2;
Printf.printf "%d common items retained in %.2f seconds.\n" (length s1) (Sys.time () -. t1);;


