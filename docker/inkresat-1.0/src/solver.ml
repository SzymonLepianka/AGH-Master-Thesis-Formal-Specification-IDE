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

(*let agenda = Agenda.create()*)
let agenda_blocked = AgendaBlocked.create()

let clauses_added = ref 0
let vars_added_tmp = ref 0
let clauses_added_tmp = ref 0
let sat_cycles = ref 0
let feeding_time = ref 0.0
let solving_time = ref 0.0
let time_tmp = ref 0.0

let add_lclause ls =
  let _ = Minisat.add_lclause ls in clauses_added := !clauses_added+1

let add_unit_lclause l =
  let _ = Minisat.add_unit_lclause l in clauses_added := !clauses_added+1

let add_binary_lclause l m =
  let _ = Minisat.add_binary_lclause l m in clauses_added := !clauses_added+1

let add_ternary_lclause l m n =
  let _ = Minisat.add_ternary_lclause l m n in clauses_added := !clauses_added+1

let propagate_boxes boxes rel pref dia_lit =
  List.iter
    (if !Options.eager_det then begin
       fun (m, body_id) ->
	 add_ternary_lclause
	   (Minisat.negate dia_lit) (Minisat.negate m) (get_lit dia_lit body_id);
	 if !Options.transitive then
	   add_ternary_lclause
	     (Minisat.negate dia_lit) (Minisat.negate m)
	     (get_lit dia_lit (Hashtbl.find Preprocessor.fml_to_id (Box(rel,body_id))));
	 if HashSet.mem pending_fmls (dia_lit,body_id) then Agenda.add agenda dia_lit body_id
     end else
       fun (m, body_id) ->
	 add_ternary_lclause
	   (Minisat.negate dia_lit) (Minisat.negate m) (get_lit dia_lit body_id);
	 if !Options.transitive then
	   add_ternary_lclause
	     (Minisat.negate dia_lit) (Minisat.negate m)
	     (get_lit dia_lit (Hashtbl.find Preprocessor.fml_to_id (Box(rel,body_id)))))
    (Hashtbl.find_all boxes.(rel) pref)

let propagate_box_to diamonds blocked rel pref box_lit body_id =
  List.iter
    (if !Options.eager_det then begin
       fun m ->
	 add_ternary_lclause
	   (Minisat.negate m) (Minisat.negate box_lit) (get_lit m body_id);
	 if !Options.transitive then
	   add_ternary_lclause
	     (Minisat.negate m) (Minisat.negate box_lit)
	     (get_lit m (Hashtbl.find Preprocessor.fml_to_id (Box(rel,body_id))));
	 if HashSet.mem pending_fmls (m,body_id) then Agenda.add agenda m body_id
     end else
       fun m ->
	 add_ternary_lclause
	   (Minisat.negate m) (Minisat.negate box_lit) (get_lit m body_id);
	 if !Options.transitive then
	   add_ternary_lclause
	     (Minisat.negate m) (Minisat.negate box_lit)
	     (get_lit m (Hashtbl.find Preprocessor.fml_to_id (Box(rel,body_id)))))
    (Hashtbl.find_all diamonds.(rel) pref);
  if !Options.blocking then begin
    List.iter
      (if !Options.eager_det then begin
	 fun (dia_id,_) ->
	   Agenda.add agenda pref dia_id;
	   HashSet.add pending_fmls (pref,dia_id);
	   Hashtbl.remove blocked.(rel) pref
       end else
	 fun (dia_id,_) ->
	   HashSet.add pending_fmls (pref,dia_id);
	   Hashtbl.remove blocked.(rel) pref)
      (Hashtbl.find_all blocked.(rel) pref);
    if !Options.subtree_blocking then Patterns.unblock_subtree pref
  end

let rec feed_sat diamonds boxes blocked =
  match Agenda.pop agenda with
      None -> ()
    | Some (p, fml_id) ->
	(*print_endline ("processing " ^ Minisat.string_of_lit p ^ ":" ^ string_of_id fml_id);*)
	if !Options.subtree_blocking && Prefixes.is_blocked p then
	  Prefixes.block_formula p fml_id
	else
	(match DynArray.get id_to_fml fml_id with
	   | And ids ->
	       let l = to_lit p fml_id in
		 List.iter (add_binary_lclause (Minisat.negate l))
		   (if !Options.eager_prop || !Options.eager_det then
		       List.rev_map (fun id -> 
			 let l = get_lit p id in
			   if HashSet.mem pending_fmls (p,id) then Agenda.add agenda p id;
			   l) ids
		    else List.rev_map (get_lit p) ids)
	   | Or ids -> 
	       add_lclause
		 (Minisat.negate (to_lit p fml_id) ::
		    (if !Options.eager_prop then
		        List.rev_map (fun id -> 
			  let l = get_lit p id in
			    if HashSet.mem pending_fmls (p,id) then Agenda.add agenda p id;
			    l) ids
		     else List.rev_map (get_lit p) ids))
	   | Dia (r, id) ->
	       let pat = ref Patterns.dummy_pattern in
		 if not(!Options.blocking) ||
		   (pat := Patterns.get_pattern p r id; not(Patterns.is_expanded !pat))
		 then begin
		   let l = to_lit p fml_id in
		     add_binary_lclause (Minisat.negate l) (new_lit l id);
		     if !Options.eager_det && HashSet.mem pending_fmls (l,id) then
		       Agenda.add agenda l id;
		     propagate_boxes boxes r p l;
		     Hashtbl.add diamonds.(r) p l;
		     if !Options.blocking then
		       (Patterns.add_diamond p l r id; Patterns.expand !pat)
		 end else Hashtbl.add blocked.(r) p (fml_id,id)
	   | Box (r, id) ->
	       let l = to_lit p fml_id in
		 if !Options.reflexive then begin
		   add_binary_lclause (Minisat.negate l) (get_lit p id);
		   if !Options.eager_det && HashSet.mem pending_fmls (p,id) then
		     Agenda.add agenda p id
		 end;
		 Hashtbl.add boxes.(r) p (l,id);
		 if !Options.blocking then Patterns.add_box p r id;
		 propagate_box_to diamonds blocked r p l id
	   | False -> add_unit_lclause (Minisat.negate (to_lit p fml_id))
	   | _ -> raise (Assertion "agenda must not not contain TRUE or literals"));
	HashSet.remove pending_fmls (p,fml_id);
	feed_sat diamonds boxes blocked

let rec feed_sat_blocked diamonds boxes =
  match AgendaBlocked.pop agenda_blocked with
      None -> ()
    | Some (p, fml_id, r, body_id, pattern) ->
	let l = to_lit p fml_id in
	  add_binary_lclause (Minisat.negate l) (get_lit l body_id);
	  propagate_boxes boxes r p l;
	  Hashtbl.add diamonds.(r) p l;
	  Patterns.add_diamond p l r body_id;
	  Patterns.expand pattern;
	  feed_sat_blocked diamonds boxes

(*let rec print_model prefs =
  if not(prefs = []) then
    let _,prefs = DynArray.fold_left
      (fun (i,a) fml ->
	 try
	   print_endline (Minisat.string_of_lit(List.hd prefs) ^ ":" ^ string_of_id i
			  ^ " (" ^ Minisat.string_of_lit(to_lit (List.hd prefs) i)
			  ^ ") -- "
			  ^ string_of_bool(Minisat.is_lit_true (to_lit (List.hd prefs) i)));
	   (i+1,match fml with
		Dia _ ->
		  if Minisat.is_lit_true (to_lit (List.hd prefs) i) && not(Prefixes.is_blocked (to_lit (List.hd prefs) i))
		  then to_lit (List.hd prefs) i::a else a
	      | _ -> a)
	 with Not_found -> (i+1,a))
      (0,List.tl prefs) id_to_fml
    in print_model prefs*)

let rec loop diamonds boxes blocked =
  if !Options.verbosity>=2 then begin
    vars_added_tmp := !vars_added;
    clauses_added_tmp := !clauses_added;
  end;

  if Agenda.is_empty agenda && (not !Options.blocking || AgendaBlocked.is_empty agenda_blocked)
  then true else begin
    if !Options.blocking then feed_sat_blocked diamonds boxes;
    feed_sat diamonds boxes blocked;
    Agenda.clear agenda;

    if !Options.verbosity>=2 then begin
      print_endline ("added "
		     ^ string_of_int (!vars_added - !vars_added_tmp)
		     ^ " variables and "
		     ^ string_of_int (!clauses_added - !clauses_added_tmp)
		     ^ " clauses");
      if !Options.verbosity>=3 then
	let tmp = Unix.gettimeofday() in
	  feeding_time := !feeding_time +. tmp -. !time_tmp;
	  time_tmp := tmp
    end;

    let extends = !Options.try_extending && Minisat.extend() in
    let sat = extends || Minisat.solve() in
      (* print_model [Minisat.dummy_lit]; *)

      if !Options.verbosity>=3 then begin
	let tmp = Unix.gettimeofday() in
	  solving_time := !solving_time +. tmp -. !time_tmp;
	  time_tmp := tmp
      end;
      sat_cycles := !sat_cycles+1;

      if sat then begin
	if !Options.blocking && not extends then begin
	  Patterns.update_from_model();
	  Array.iteri
	    (fun r ht ->
	       let blocked_per_rel =
		 Hashtbl.fold
		   (if !Options.subtree_blocking then
		      (fun p (id,id') a ->
			 if Prefixes.is_blocked p then (Prefixes.block_formula p id; a)
			 else (p,id,id')::a)
		    else fun p (id,id') a -> (p,id,id')::a)
		   ht []
	       in
		 Hashtbl.clear ht;
		 List.iter
		   (fun (p, fml_id, body_id) ->
		      let pat = Patterns.get_pattern p r body_id in
			if Minisat.is_lit_true (to_lit p fml_id)
			  && not(Patterns.is_expanded pat)
			then AgendaBlocked.add agenda_blocked p fml_id r body_id pat
			else Hashtbl.add ht p (fml_id,body_id))
		   blocked_per_rel)
	    blocked
	end;
	(*print_model [Minisat.dummy_lit];*)
	HashSet.iter (fun (p,fml_id) ->
			if Minisat.is_lit_true (to_lit p fml_id)
			then Agenda.add agenda p fml_id) pending_fmls;
	loop diamonds boxes blocked
      end else false
  end

let solve fml_id =
  match DynArray.get id_to_fml fml_id with
      Lit -> true
    | True -> true
    | _ ->
	Minisat.init (if !Options.verbosity>1 then !Options.verbosity-1 else 0);
	vars_added := 0;
	clauses_added := 0;
	sat_cycles := 0;
	feeding_time := 0.0;
	solving_time := 0.0;
	if !Options.verbosity>=1 then
	  time_tmp := Unix.gettimeofday();
	Agenda.clear agenda;
	AgendaBlocked.clear agenda_blocked;
	HashSet.clear pending_fmls;
	HashSet.clear new_lits;
	Hashtbl.clear pfml_to_var;
	Agenda.add agenda (Minisat.dummy_lit) fml_id;
	let diamonds = Array.init !num_rels (fun _ -> Hashtbl.create 100) in
	let boxes = Array.init !num_rels (fun _ -> Hashtbl.create 100) in
	let blocked = Array.init !num_rels (fun _ -> Hashtbl.create 100) in
	let l = new_lit (Minisat.dummy_lit) fml_id in
	  if !Options.blocking then begin
	    Prefixes.init();
	    Patterns.init (DynArray.length id_to_fml) boxes
	  end;
	  add_unit_lclause l;
	  let v = loop diamonds boxes blocked in
	    if !Options.verbosity>0 then begin
	      print_endline ("variables added: " ^ string_of_int !vars_added);
	      print_endline ("clauses added: " ^ string_of_int !clauses_added);
	      print_endline ("SAT cycles: " ^ string_of_int !sat_cycles);
	      if !Options.blocking then begin
		print_endline ("pattern store hits: "
			       ^ string_of_int !Patterns.patterns_blocked);
		print_endline ("pattern store misses: "
			       ^ string_of_int !Patterns.patterns_new)
	      end;
	      if !Options.verbosity>=3 then	      
		(print_endline ("feeding time: " ^ string_of_float !feeding_time);
		 print_endline ("solving time: " ^ string_of_float !solving_time))
	      else print_endline ("time: " ^ string_of_float (Unix.gettimeofday() -. !time_tmp))
	    end; v
