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

let new_lits = HashSet.create 1000 Minisat.dummy_lit
let pending_fmls = HashSet.create 1000 (Minisat.dummy_lit,0)
let pfml_to_var = Hashtbl.create 1000

let agenda = Agenda.create()

let vars_added = ref 0

exception Assertion of string

let to_lit pref fml_id =
  if fml_id land 1 = 0 then Minisat.to_lit (Hashtbl.find pfml_to_var (pref,fml_id)) true
  else Minisat.to_lit (Hashtbl.find pfml_to_var (pref,fml_id-1)) false

let new_lit pref fml_id =
  let v = Minisat.add_var() in
  let is_not_Lit = match DynArray.get id_to_fml fml_id with Lit -> false | True -> false | _ -> true in
    Minisat.freeze_var v;
    vars_added := !vars_added+1;
    if is_not_Lit then HashSet.add pending_fmls (pref,fml_id);
    if fml_id land 1 = 0 then
      (Hashtbl.add pfml_to_var (pref,fml_id) v;
       if is_not_Lit then HashSet.add new_lits (Minisat.to_lit v false);
       Minisat.to_lit v true)
    else (Hashtbl.add pfml_to_var (pref,fml_id-1) v;
	  if is_not_Lit then HashSet.add new_lits (Minisat.to_lit v true);
	  Minisat.to_lit v false)

let get_lit pref fml_id =
  try let l = to_lit pref fml_id in
    if HashSet.mem new_lits l then
      (HashSet.add pending_fmls (pref,fml_id); HashSet.remove new_lits l);
    l
  with Not_found -> new_lit pref fml_id
