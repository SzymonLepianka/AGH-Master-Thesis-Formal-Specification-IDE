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

type rel = int

type xformula =
    XVar of int
  | XTrue
  | XFalse
  | XNeg of xformula
  | XAnd of xformula list
  | XOr of xformula list
  | XDia of rel * xformula
  | XBox of rel * xformula

type iformula =
    Lit (* of var *)
  | True
  | False
  | And of int list
  | Or of int list
  | Dia of rel * int
  | Box of rel * int

let num_rels = ref 0
let num_vars = ref 0
let id_to_fml = DynArray.create()

let var_of_string = Hashtbl.create 100
let rel_of_string = Hashtbl.create 100

let init () =
  num_rels := 0;
  num_vars := 0;
  Hashtbl.clear var_of_string;
  Hashtbl.clear rel_of_string;
  DynArray.clear id_to_fml

let rec string_of_id n = match DynArray.get id_to_fml n with
    Lit -> (if n land 1 == 0 then string_of_int n else "-" ^ string_of_int(n-1))
  | And ids ->
      (match ids with
	   [] -> "#true#"
	 | _ -> "(" ^ String.concat " & " (List.map string_of_id ids) ^ ")")
  | Or ids ->
      (match ids with
	   [] -> "#false#"
	 | _ -> "(" ^ String.concat " | " (List.map string_of_id ids) ^ ")")
  | Dia (r, n) -> "<" ^ string_of_int r ^ ">" ^ string_of_id n
  | Box (r, n) -> "[" ^ string_of_int r ^ "]" ^ string_of_id n
  | True -> "true"
  | False -> "false"
