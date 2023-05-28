(* code to be appended to hashset.ml in
 * http://www.lri.fr/~filliatr/ftp/ocaml/ds/hashset.ml
 * to have a generic interface similar to the one in HashSet
 *)

let length h = h.size


(* set operations, inplace *)    
let update h other =
  let add_to key = add h key in
  iter add_to other;;

let diff_update h other =
  iter (fun x -> remove h x) other;;

let inter_update h other =
  let f key =
      begin if mem other key then ()  else
        remove h key
      end
  in
  iter f h;;

(* set operations, not inplace *)
let union h other =
  let r = ref (copy h) in
  update !r other;
  !r;;

let diff h other =
  let r = ref (copy h) in
  diff_update !r other;
  !r

let inter h other =
  let r = ref (copy h) in
  inter_update !r other;
  !r

let equal h other =
  if h.size <> other.size then
    false 
  else
    let memq key =
      if (not (mem other key)) then
        raise Not_found 
    in
    let res = ref true in
    let _ = try iter memq h with
    | Not_found -> ignore(res := false) in
    !res

