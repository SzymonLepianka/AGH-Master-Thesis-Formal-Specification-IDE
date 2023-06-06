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

(*type formula =
    IVar of int
  | ITrue
  | IFalse
  | IAnd of formula list
  | IOr of formula list
  | IDia of int * formula
  | IBox of int * formula*)

(*let var_to_id = Array.make !num_vars None*)
let var_to_id = Hashtbl.create !num_vars
let fml_to_id = Hashtbl.create 1000

exception Assertion of string
exception Trivial

let rec split ys zs xs = match xs with
    [] -> (ys,zs)
  | x::xr -> split zs (x::ys) xr

let id_less id id' =
  if id land 1 = 0 && id' = id+1 then raise Trivial else id<id'

let rec merge xs ys = match xs,ys with
    [], ys -> ys
  | xs, [] -> xs
  | x::xr, y::yr ->
      if id_less x y then x :: merge xr ys
      else if id_less y x then y :: merge xs yr
      else x :: merge xr yr

let rec msort xs = match xs with
    [] -> []
  | [_] -> xs
  | _ -> let ys, zs = split [] [] xs in merge (msort ys) (msort zs)

let get_index fml fml' =
  try Hashtbl.find fml_to_id fml with
      Not_found ->
	let id = DynArray.length id_to_fml in
	  Hashtbl.add fml_to_id fml id;
	  Hashtbl.add fml_to_id fml' (id+1);
	  DynArray.add id_to_fml fml;
	  DynArray.add id_to_fml fml';
	  id

let flatten_and =
  List.fold_left (fun a id -> match DynArray.get id_to_fml id with
		      And ids -> List.rev_append ids a
		    | _ -> id::a) []

let flatten_or =
  List.fold_left (fun a id -> match DynArray.get id_to_fml id with
		      Or ids -> List.rev_append ids a
		    | _ -> id::a) []

let negate_id id = if id land 1 = 0 then id+1 else id-1

let rec simplify_and ids =
  try match msort ids with
      [] -> 0                     (* True *)
    | 0::idr -> simplify_and idr  (* True & formula ~> formula *)
    | 1::_ -> 1                   (* False & formula ~> False *)
    | [id] -> id                  (* unit conjunction *)
    | ids -> get_index (And ids) (Or (List.map negate_id ids))
  with Trivial -> 1               (* p & ~p ~> False *)

let rec simplify_or ids =
  try match msort ids with
      [] -> 1                     (* False *)
    | 1::idr -> simplify_or idr   (* False | formula ~> formula *)
    | 0::_ -> 0                   (* True | formula ~> True *)
    | [id] -> id                  (* unit disjunction *)
    | ids -> get_index (Or ids) (And (List.map negate_id ids))
  with Trivial -> 0               (* p | ~p ~> True *)

let simplify_dia r id =
  if id = 1 then 1 else get_index (Dia(r,id)) (Box(r,negate_id id))

let simplify_box r id =
  if id = 0 then 0 else get_index (Box(r,id)) (Dia(r,negate_id id))

let rec index fml = match fml with
    XVar n -> (try Hashtbl.find var_to_id n with
		   Not_found ->
		     let id = DynArray.length id_to_fml in
		       Hashtbl.add var_to_id n id;
		       DynArray.add id_to_fml Lit;
		       DynArray.add id_to_fml Lit;
		       id)
  | XNeg fml -> negate_id (index fml)
  | XTrue -> 0
  | XFalse -> 1
  | XAnd fmls -> simplify_and(flatten_and(List.rev_map index fmls))
  | XOr fmls -> simplify_or(flatten_or(List.rev_map index fmls))
  | XDia (r,fml) -> simplify_dia r (index fml)
  | XBox (r,fml) -> simplify_box r (index fml)

let parse_formula input =
  Syntax.init ();
  let lexbuf = Lexing.from_channel input in
    (*DynArray.clear id_to_fml;*)
    DynArray.add id_to_fml True;
    DynArray.add id_to_fml False;
    Hashtbl.clear var_to_id;
    Hashtbl.clear fml_to_id;
    Hashtbl.add fml_to_id True 0;
    Hashtbl.add fml_to_id False 1;
    index(Parser.file Lexer.lexFormula lexbuf)
