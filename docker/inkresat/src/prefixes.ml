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

(*type blocking = DIRECT | TRANSITIVE*)

let blocked_fmls = Hashtbl.create 1000
let prefix_tree = Hashtbl.create 1000
let blocked_prefs = HashSet.create 1000 Minisat.dummy_lit
(*let blocked_prefs = Hashtbl.create 1000*)

let init () =
  Hashtbl.clear blocked_fmls;
  Hashtbl.clear prefix_tree;
  HashSet.clear blocked_prefs

let add_successor parent child rel body_id =
  Hashtbl.add prefix_tree parent (child,rel,body_id)

let successors = Hashtbl.find_all prefix_tree

let is_blocked = HashSet.mem blocked_prefs

(*let is_directly_blocked pref =
  try Hashtbl.find blocked_prefs pref = DIRECT with Not_found -> false

let is_transitively_blocked pref =
  try Hashtbl.find blocked_prefs pref = TRANSITIVE with Not_found -> false*)

let unblock = HashSet.remove blocked_prefs

let block_formula = Hashtbl.add blocked_fmls

let pop_and_process_blocked_fmls f pref =
  List.iter
    (fun id -> f pref id; Hashtbl.remove blocked_fmls pref)
    (Hashtbl.find_all blocked_fmls pref)

let rec block_subtree pref =
  if not(HashSet.mem blocked_prefs pref) then HashSet.add blocked_prefs pref;
  List.iter (fun (p,_,_) -> block_subtree p) (Hashtbl.find_all prefix_tree pref)

(*let is_successor pref p r id = List.mem (p,r,id) (Hashtbl.find_all prefix_tree pref)*)
