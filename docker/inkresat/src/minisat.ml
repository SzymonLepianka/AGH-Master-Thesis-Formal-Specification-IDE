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

external init               : int -> unit  = "init"
external add_var            : unit -> int = "addVar"
external add_unit_clause    : int -> bool -> bool = "addUnitClause"
external add_binary_clause  : int -> bool -> int -> bool -> bool = "addBinaryClause"
external add_ternary_clause : int -> bool -> int -> bool -> int -> bool -> bool
  = "addTernaryClause_bytecode" "addTernaryClause_native"
external solve              : unit -> bool = "solve"
external is_var_true        : int -> bool = "isVarTrue"
external freeze_var         : int -> unit = "freezeVar"
external thaw_var           : int -> unit = "thawVar"

type lit = int

let dummy_lit = -1

let to_lit v vp = if vp then v+v else v+v+1
let negate n = if n land 1 = 0 then n+1 else n-1

external add_unit_lclause    : lit -> bool = "addUnitLClause"
external add_binary_lclause  : lit -> lit -> bool = "addBinaryLClause"
external add_ternary_lclause : lit -> lit -> lit -> bool = "addTernaryLClause"
external add_lclause         : lit list -> bool = "addLClause"
external is_lit_true         : lit -> bool = "isLitTrue"
external extend              : unit -> bool = "extend"
external solve_assuming      : lit list -> bool = "solveAssuming"

let string_of_lit = string_of_int
