(*****************************************************************************
 *  Author:
 *    Mark Kaminski <kaminski@ps.uni-saarland.de>
 *
 *  Copyright:
 *    Mark Kaminski, 2012
 *
 *  This file is part of InKreSAT.
 *
 *  Permission is hereby granted, free of charge, to any person obtaining
 *  a copy of this software and associated documentation files (the
 *  "Software"), to deal in the Software without restriction, including
 *  without limitation the rights to use, copy, modify, merge, publish,
 *  distribute, sublicense, and/or sell copies of the Software, and to
 *  permit persons to whom the Software is furnished to do so, subject to
 *  the following conditions:
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *  OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *  WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *****************************************************************************)

open Syntax
open SolverAux

type prefix = Minisat.lit
type pattern = int list

(*let diamonds = ref []*)
let boxes = ref (Array.init 0 Hashtbl.create)
let active_boxes = ref (Array.init 0 Hashtbl.create)
let patterns = ref (Array.make 0 [])
let counter = ref 0
let patterns_new = ref 0
let patterns_blocked = ref 0
let active_rels = Hashtbl.create 1000
let active_diamonds = ref (Array.init 0 Hashtbl.create)

let dummy_pattern = []

let init n bs =
  counter := 0; (*diamonds := [];*) boxes := bs;
  patterns_new := 0; patterns_blocked := 0;
  patterns := Array.make n [];
  active_boxes := Array.init !num_rels (fun _ -> Hashtbl.create 10);
  if !Options.eager_blocking then
    active_diamonds := Array.init !num_rels (fun _ -> Hashtbl.create 10);
  Hashtbl.clear active_rels

let activate_boxes pref rel =
  let aboxes = Hashtbl.find_all (!active_boxes).(rel) pref in
    List.iter (fun (l,id) -> if not(List.mem id aboxes) && Minisat.is_lit_true l then
		 Hashtbl.add (!active_boxes).(rel) pref id)
      (Hashtbl.find_all (!boxes).(rel) pref)

let get_pattern pref rel body_id =
(*  let l =*)
  if not(List.mem rel (Hashtbl.find_all active_rels pref)) then
    (activate_boxes pref rel; Hashtbl.add active_rels pref rel);
  let box_ids =
    if !Options.transitive then
      List.fold_left
	(fun a id -> Hashtbl.find Preprocessor.fml_to_id (Box(rel,id)) :: id :: a)
	[] (Hashtbl.find_all (!active_boxes).(rel) pref)
    else Hashtbl.find_all (!active_boxes).(rel) pref in
    if List.mem body_id box_ids then box_ids else body_id :: box_ids
(*  in
    print_endline ("requesting the pattern for " ^ Minisat.string_of_lit pref ^ ":<" ^ string_of_int rel
		   ^ ">" ^ string_of_id body_id ^ " -> "
		   ^ String.concat ", "
		   (List.map string_of_id
		      (let box_ids = if !Options.transitive then
			 List.fold_left
			   (fun a id -> Hashtbl.find Preprocessor.fml_to_id (Box(rel,id)) :: id :: a)
			   [] (Hashtbl.find_all (!active_boxes).(rel) pref)
		       else Hashtbl.find_all (!active_boxes).(rel) pref in
			 if List.mem body_id box_ids then box_ids else body_id :: box_ids)));
    l*)

let expand ids =
  (*print_endline ("expanding the pattern "
		 ^ String.concat ", " (List.map string_of_id ids)
		 ^ " -> " ^ string_of_int !counter);*)
  List.iter (fun id -> Array.set !patterns id (!counter :: Array.get !patterns id)) ids;
  counter := !counter+1

let add_diamond pref lit rel body_id =
  (*if Prefixes.is_successor pref lit rel body_id then raise (Assertion "trying to add a diamond twice!");*)
  (*diamonds := (pref,lit,rel,body_id) :: !diamonds;*)
  Prefixes.add_successor pref lit rel body_id;
  if !Options.eager_blocking then Hashtbl.add (!active_diamonds).(rel) pref body_id

exception Empty

let rec drop xs ys = match xs, ys with
    [], _ -> raise Empty
  | _, [] -> raise Empty
  | x::xr, y::yr ->
      if x<y then drop xs yr
      else if x>y then drop xr ys
      else (xs, ys)

let rec search first old lists = match first, lists with
  | x::_, [] ->
      (match List.rev old with
	   (y::yr)::lr -> x=y || search first [] (yr::lr)
	 | _ -> raise (Assertion "Patterns.search 1"))
  | _::_, next::lr -> let fst, nxt = drop first next in search fst (nxt::old) lr
  | [], _ -> false

let is_expanded_ ids =
    match ids with
	[y] -> Array.get !patterns y <> []
      | y::ys ->
	  (try search (Array.get !patterns y) [] (List.rev_map (Array.get !patterns) ys)
	   with Empty -> false)
      | [] -> raise (Assertion "patterns must be nonempty")

let is_expanded ids =
  (*print_string ("checking for expansion: " ^ String.concat ", " (List.map string_of_id ids) ^ " -> ");*)
  if
    match ids with
	[y] -> Array.get !patterns y <> []
      | y::ys ->
	  (try search (Array.get !patterns y) [] (List.rev_map (Array.get !patterns) ys)
	   with Empty -> false)
      | [] -> raise (Assertion "patterns must be nonempty")
  then (patterns_blocked := !patterns_blocked + 1; (*print_endline "blocked";*) true)
  else (patterns_new := !patterns_new + 1; (*print_endline "new";*) false)

let rec update_patterns' a pref =
  let a = List.fold_left
    (fun a (lit,r,body_id) ->
       if Minisat.is_lit_true lit then begin
	 (*if Minisat.is_lit_true (to_lit lit body_id) then begin*)
	 let pat = get_pattern pref r body_id in
	   if is_expanded pat then (Prefixes.block_subtree lit; a)
	   else begin
	     if !Options.eager_blocking then
	       Hashtbl.add (!active_diamonds).(r) pref body_id;
	     expand pat;
	     if Prefixes.is_blocked lit then begin
	       Prefixes.unblock lit;
	       Prefixes.pop_and_process_blocked_fmls
		 (if !Options.agenda then
		    fun p id -> HashSet.add pending_fmls (p,id)
		  else Agenda.add agenda) lit;
	     end;
	     lit::a
	   end
	 (*end else raise (Assertion "Patterns.update_patterns");*)
       end else (Prefixes.block_subtree lit; a)) a (Prefixes.successors pref)
  in match a with p::pr -> update_patterns' pr p | _ -> ()

let update_patterns = update_patterns' []

(*let rec update_patterns pref =
  List.iter
    (fun (lit,r,body_id) ->
       if Minisat.is_lit_true lit then begin
	 (*if Minisat.is_lit_true (to_lit lit body_id) then begin*)
	 let pat = get_pattern pref r body_id in
	   if is_expanded pat then Prefixes.block_subtree lit
	   else begin
	     if !Options.eager_blocking then
	       Hashtbl.add (!active_diamonds).(r) pref body_id;
	     expand pat;
	     if Prefixes.is_blocked lit then begin
	       Prefixes.unblock lit;
	       Prefixes.pop_and_process_blocked_fmls
		 (if !Options.agenda then
		    fun p id -> HashSet.add pending_fmls (p,id); Agenda.add agenda p id
		  else Agenda.add agenda) lit;
	     end;
	     update_patterns lit
	   end
	 (*end else raise (Assertion "Patterns.update_patterns");*)
       end else Prefixes.block_subtree lit) (Prefixes.successors pref)*)

let rec unblock_subtree pref =
  List.iter
    (fun (lit,r,body_id) ->
       if Prefixes.is_blocked lit && Minisat.is_lit_true lit then
	 let pat = get_pattern pref r body_id in
	   if not(is_expanded pat) then begin
	     expand pat;
	     Prefixes.unblock lit;
	     Prefixes.pop_and_process_blocked_fmls
	       (if !Options.agenda then
		  fun p id -> HashSet.add pending_fmls (p,id); Agenda.add agenda p id
		else Agenda.add agenda) lit;
	     unblock_subtree lit
	   end)
    (Prefixes.successors pref)

let rec iter_prefs f p =
  List.iter (fun v -> f p v; match v with succ,_,_ -> iter_prefs f succ)
    (Prefixes.successors p)

let update_from_model () =
  counter := 0;
  Array.iteri (fun i _ -> Hashtbl.clear (!active_boxes).(i)) !active_boxes;
  Array.iteri (fun i _ -> (!patterns).(i) <- []) !patterns;
  Hashtbl.clear active_rels;
  Array.iteri (fun i _ -> Hashtbl.clear (!active_diamonds).(i)) !active_diamonds;
  (*HashSet.clear blocked_prefs;*)
  if !Options.subtree_blocking then
    update_patterns Minisat.dummy_lit
  else
    iter_prefs
      (fun p (lit,r,body_id) ->
	 if Minisat.is_lit_true lit then begin
	   if !Options.eager_blocking then
	     Hashtbl.add (!active_diamonds).(r) p body_id;
	   if Minisat.is_lit_true (to_lit lit body_id) then
	     expand(get_pattern p r body_id)
	 end) Minisat.dummy_lit

let extend_patterns pref rel =
  List.iter
    (fun id ->
       let pat = get_pattern pref rel id in
	 if not(is_expanded pat) then expand pat)
    (Hashtbl.find_all (!active_diamonds).(rel) pref)

(* calling add_box twice for the same box may result in pattern lists being not strictly ordered *)

let add_box pref rel body_id =
  Hashtbl.add (!active_boxes).(rel) pref body_id;
  (*if !Options.transitive then Hashtbl.add (!active_boxes).(rel) pref fml_id;*)
  if !Options.eager_blocking then extend_patterns pref rel
