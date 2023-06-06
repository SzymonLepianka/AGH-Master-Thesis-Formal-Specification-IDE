(*****************************************************************************
 *  Authors:
 *    Mark Kaminski <kaminski@ps.uni-saarland.de>
 *    Tobias Tebbi <ttebbi@ps.uni-saarland.de>
 *
 *  Copyright:
 *    Mark Kaminski, Tobias Tebbi, 2012
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

let starting_time = ref 0.0

let execname = "inkresat"	

let solve id =
  if Solver.solve id then
    print_string "SATISFIABLE"
  else print_string "UNSATISFIABLE";
  if !Options.scripting_output then
    Printf.printf ",%-.2f\n" (Unix.gettimeofday() -. !starting_time)
  else print_endline ""
	
let _ =
  Arg.parse
    ["-i", Arg.Unit (fun () ->
		       solve(Preprocessor.parse_formula stdin)),
     "   read from the standard input";
     "-v", Arg.Int (fun n -> Options.verbosity := n),
     "n  set verbosity level to n (min: 0, default: 1, max: 3)";
     "-c", Arg.Unit (fun () ->
		       starting_time := Unix.gettimeofday();
		       Options.verbosity := 0;
		       Options.scripting_output := true),
     "   produce compact output suitable for benchmarking";
     "-op", Arg.Unit (fun () ->
		       Options.eager_prop := true;
		       Options.eager_det := true;
		       Options.blocking := false;
		       Options.subtree_blocking := false),
     "  one-phase translation (not recommended, off by default)";
     "-ed", Arg.Unit (fun () -> Options.eager_det := true),
     "  eager decomposition of deterministic formulas (off by default)";
     "-eb", Arg.Unit (fun () -> Options.eager_blocking := true),
     "  eager blocking (off by default)";
     "-dd", Arg.Unit (fun () -> Options.agenda := true),
     "  delay decomposition of disjunctions (off by default)";
     "-nb", Arg.Unit (fun () -> Options.blocking := false; Options.subtree_blocking := false),
     "  disable pattern-based blocking (on by default)";
     "-nsb", Arg.Unit (fun () -> Options.subtree_blocking := false),
     " disable subtree blocking (on by default)";
     "-ne", Arg.Unit (fun () -> Options.try_extending := false),
     "  make SAT cycles independent (default: try to extend the current branch)";
     "-r", Arg.Unit (fun () -> Options.reflexive := true),
     "   interpret all relations as being reflexive";
     "-t", Arg.Unit (fun () -> Options.transitive := true),
     "   interpret all relations as being transitive"]
    
    (fun file -> solve(Preprocessor.parse_formula(open_in file)))

    ("Usage: " ^ execname ^ " [options] [filename]")

(* let _ =
  let id = Preprocessor.parse_formula stdin in
    (* print_endline (string_of_id id); *)
    (* print_endline "solving..."; *)
    if Solver.solve id then
      print_endline "SATISFIABLE"
    else print_endline "UNSATISFIABLE" *)
